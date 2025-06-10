package dev.cgs.mc.charity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.breakfastquay.rubberband.RubberBandLiveShifter;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.PitchShifter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audio.AudioConverter;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.opus.OpusEncoder;

public class VoicePlugin implements VoicechatPlugin, Listener {

  public static VoicechatApi voicechatApi;

  @Nullable
  public static VoicechatServerApi voicechatServerApi;

  public static class VoiceChangerState {
    public OpusDecoder decoder;
    public OpusEncoder encoder;
    public RubberBandLiveShifter rubberband;
    public float[] inputBuffer;
    public int inputSize;
    public float[] outputBuffer;
    public int outputSize;

    VoiceChangerState() {
      encoder = voicechatApi.createEncoder();
      decoder = voicechatApi.createDecoder();
      rubberband = new RubberBandLiveShifter(48000, 1, 0);
      inputBuffer = new float[2048];
      outputBuffer = new float[2048];
      inputSize = 0;
      outputSize = 0;
    }

    public void dispose() {
      encoder.close();
      decoder.close();
      rubberband.dispose();
    }
  }
  private static ConcurrentHashMap<UUID, VoiceChangerState> state;

  @EventHandler
  public void onLogout(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    if (!state.containsKey(uuid)) {
      return;
    }
    state.get(uuid).dispose();
    state.remove(uuid);
  }

  @EventHandler
  public void onLogin(PlayerLoginEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    VoicePlugin.setPitchScale(uuid, 1.5);
  }

  @Override
  public void initialize(VoicechatApi api) {
      voicechatApi = api;
      state = new ConcurrentHashMap<>();
  }

  @Override
  public String getPluginId() {
      return CharityMain.PLUGIN_ID;
  }

  public static void setPitchScale(UUID who, double scale) {
    VoiceChangerState v = VoicePlugin.getVoiceChangerState(who);
    v.rubberband.setPitchScale(scale);
  }

  public static VoiceChangerState getVoiceChangerState(UUID who) {
    if (!state.containsKey(who)) {
      VoiceChangerState v = new VoiceChangerState();
      state.put(who, v);
    }
    return state.get(who);
  }

  @Override
  public void registerEvents(EventRegistration registration) {
      registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
      registration.registerEvent(MicrophonePacketEvent.class, this::onMicPacket);
  }

  private void onServerStarted(VoicechatServerStartedEvent event) {
      voicechatServerApi = event.getVoicechat();
  }

  private void onMicPacket(MicrophonePacketEvent event) {
    VoicechatConnection senderConnection = event.getSenderConnection();
    if (senderConnection == null) {
        return;
    }

    UUID who = senderConnection.getPlayer().getUuid();
    VoiceChangerState v = getVoiceChangerState(who);

    // don't bother if the pitch scale is close
    if (Math.abs(1.0 - v.rubberband.getPitchScale()) < 0.05) {
      return;
    }

    byte[] opus = event.getPacket().getOpusEncodedData();
    if (opus.length <= 0) {
      v.decoder.resetState();
      v.encoder.resetState();
      v.inputSize = 0;
      v.outputSize = 0;
      return;
    }
    float[] decodedPCM = shortsToFloats(v.decoder.decode(opus));
    System.arraycopy(decodedPCM, 0, v.inputBuffer, v.inputSize, 960);
    v.inputSize += 960;

    // 2. Process in 512-sample blocks
    while (v.inputSize >= 512) {
        float[][] input = new float[1][512];
        System.arraycopy(v.inputBuffer, 0, input[0], 0, 512);

        float[][] output = new float[1][512];
        v.rubberband.shift(input, 0, output, 0);

        // Append output
        System.arraycopy(output[0], 0, v.outputBuffer, v.outputSize, 512);
        v.outputSize += 512;

        // Slide input buffer
        System.arraycopy(v.inputBuffer, 512, v.inputBuffer, 0, v.inputSize - 512);
        v.inputSize -= 512;
    }

    // 3. If we have enough output, encode 960 samples
    if (v.outputSize >= 960) {
        float[] toEncode = new float[960];
        System.arraycopy(v.outputBuffer, 0, toEncode, 0, 960);

        // Slide remaining output buffer
        System.arraycopy(v.outputBuffer, 960, v.outputBuffer, 0, v.outputSize - 960);
        v.outputSize -= 960;

        // Encode and store
        byte[] encoded = v.encoder.encode(floatsToShorts(toEncode));
        event.getPacket().setOpusEncodedData(encoded);
    } else {
        // Not enough data yet, encode silence
        event.getPacket().setOpusEncodedData(v.encoder.encode(new short[960]));
    }
  }
  public static float[] shortsToFloats(short[] input) {
    float[] output = new float[input.length];
    for (int i = 0; i < input.length; i++) {
        output[i] = input[i] / 32768.0f;
    }
    return output;
  }
  public static short[] floatsToShorts(float[] input) {
    short[] output = new short[input.length];
    for (int i = 0; i < input.length; i++) {
        float sample = input[i] * 32768.0f;

        // Clamp to avoid overflow
        if (sample > 32767.0f) sample = 32767.0f;
        if (sample < -32768.0f) sample = -32768.0f;

        output[i] = (short) sample;
    }
    return output;
}

}
