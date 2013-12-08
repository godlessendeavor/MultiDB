package music.mp3Player;

public class PlayingEvent {
	public static int STARTED = 1;
	public static int NEXT_SONG = 2;

	private int id;
	private long time;

	public PlayingEvent(int id,long time) {
		this.id = id;
		this.time=time;
	}
	
	public PlayingEvent(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
