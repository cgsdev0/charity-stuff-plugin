package dev.cgs.mc.charity;

import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

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

public class VoicePlugin implements VoicechatPlugin {

  public static VoicechatApi voicechatApi;

  @Nullable
  public static VoicechatServerApi voicechatServerApi;


  public class CoderPair {
    public OpusDecoder decoder;
    public OpusEncoder encoder;
    public PitchShifter shifter;
  }
  private static HashMap<UUID, CoderPair> coders;

  @Override
  public void initialize(VoicechatApi api) {
      voicechatApi = api;
      coders = new HashMap<>();
  }

  @Override
  public String getPluginId() {
      return CharityMain.PLUGIN_ID;
  }

  public CoderPair getCoders(UUID who) {
    if (!coders.containsKey(who)) {
      CoderPair pair = new CoderPair();
      pair.encoder = voicechatApi.createEncoder();
      pair.decoder = voicechatApi.createDecoder();

      int bufferSize = 960;
      int sampleRate = 48000;
      pair.shifter = new PitchShifter(1.0f, sampleRate, bufferSize, bufferSize-32);
      coders.put(who, pair);
    }
    return coders.get(who);
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
    CoderPair pair = getCoders(who);

    byte[] opus = event.getPacket().getOpusEncodedData();
    if (opus.length <= 0) {
      pair.decoder.resetState();
      pair.encoder.resetState();
      return;
    }
    short[] decodedPCM = pair.decoder.decode(opus);
    float[] floats = new float[decodedPCM.length];
    for (int i = 0; i < decodedPCM.length; i++) {
        floats[i] = decodedPCM[i] / 32768.0f;
    }

    TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(
      48000, 16, 1, true, false
    );
    AudioEvent audioEvent = new AudioEvent(format);
    audioEvent.setFloatBuffer(floats);
    pair.shifter.process(audioEvent);

    float[] outputFloat = audioEvent.getFloatBuffer();
    short[] outputPCM = new short[outputFloat.length];
    for (int i = 0; i < outputFloat.length; i++) {
        float sample = outputFloat[i];

        // Clamp first
        if (sample > 1.0f) sample = 1.0f;
        if (sample < -1.0f) sample = -1.0f;

        // Scale
        output[i] = (short)(sample * 32767.0f);
    }
    byte[] encoded = pair.encoder.encode(outputPCM);
    event.getPacket().setOpusEncodedData(encoded);
  }

}
