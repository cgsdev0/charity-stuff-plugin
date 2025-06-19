import { useStore, type Stream } from "../store.ts";

export function Footer() {
  const { streams } = useStore();
  return (
    <div>
      {streams.map((stream) => (
        <Streamer key={stream.user_id} stream={stream} />
      ))}
    </div>
  );
}

function Streamer(props: { stream: Stream }) {
  return (
    <a target="_blank" href={`https://twitch.tv/${props.stream.user_login}`}>
      <div className="twitch-avatar">
        <img src={props.stream.profile_image_url} alt={props.stream.display_name}/>
      </div>
    </a>
  );
}
