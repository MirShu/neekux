package meekux.grandar.com.meekuxpjxroject.songdata.database;

public class Constant {
	public static int brightness = 50;
    public static int[] colors =new int[3];
    public static final String ACTION_NOTIFICATION_SHOW = "org.androidpn.client.NOTIFICATION_SHOW";
	public static final String ACTION_DATEBASE_INIT_OK = "com.igoo.igoobaby.launcher.DATEBASE_INIT_OK";
	public static final String NOTIFY_BT_STATE_CHANGE = "com.igoo.igoobaby.launcher.BT_STATE_CHANGE";
	public static final String ACTION_CLOSE_PROGRESS_DIALOG = "com.igoo.igoobaby.launcher.CLOSE_PROGRESS_DIALOG";
	public static String SUCCESS_ACTION = "Success";   
	public static String CANCEL_ACTION = "Cancel";
	public static String CONNECTING_ACTION = "Connecting";
	public static String FAIL_ACTION = "Connecting";  
	
	public static final String ACTION_IGOO_INFO_PLAYING_CHANGE = "com.grandar.igoo.igooproject.IGOO_INFO_PLAYING_CHANGE";
	
	public static final String ACTION_IN_STREAM_MEDIA_MODE = "com.grandar.igoo.igooproject.IGOO_INFO_IN_STREAM_MEDIA_MODE";
	
	public static final String ACTION_UPDATE_IGOO_SOFTWARE_RSP = "com.grandar.igoo.igooproject.IGOO_INFO_UPDATE_IGOO_SOFTWARE_RSP";
	
	public static final String ACTION_IGOO_SOFTWARE_VERSION_RSP = "com.grandar.igoo.igooproject.IGOO_INFO_SOFTWARE_VERSION_RSP";

	public static final String ACTION_IGOO_BASE_INFO_RSP = "com.grandar.igoo.igooproject.IGOO_INFO_BASE_INFO_RSP";
	
	public static final String ACTION_UPDATE_IGOO_SOFTWARE_STATE = "com.grandar.igoo.igooproject.IGOO_INFO_UPDATE_IGOO_SOFTWARE_STATE";
	
	public static final String ACTION_IGOO_SHAKE_STATE = "com.grandar.igoo.igooproject.IGOO_INFO_SHAKE_STATE";

	public static final String SONG_LIGHT_EXCEPTION = "com.grandar.igoo.igooproject.SONG_LIGHT_EXCEPTION";
	
	public static final String NOTIFY_NOTIFICATION_HAS_READ = "com.grandar.igoo.igooproject.NOTIFICATION_HAS_READ";
	
	public static final String NOTIFY_MODES_ADD_TO_FAVOR_CLOSE = "com.grandar.igoo.igooproject.NOTIFY_MODES_ADD_TO_FAVOR_CLOSE";

	public static final class ConValue {

//		public static int mImageViewArray[] = { R.drawable.songlight_tab_icon,
//			    R.drawable.simplelist_tab_icon,
//				R.drawable.list_tab_icon};
//
//		public static Class mTabClassArray[] = { SongLightBrowserActivity.class,
//			SongLightSimpleListActivity.class,
//			SongLightListActivity.class};
//
//		public static int mImageViewArrayAlarm[] = { R.drawable.songlight_tab_icon,
//				R.drawable.list_tab_icon};
//
//		public static Class mTabClassArrayAlarm[] = { AlarmSongListActivity.class,
//				AlarmSongSelfListActivity.class};
		
		public static int mTotalModeNum = 9;
		
		public static int PLAY_TYPE_SINGLE_SONG_LIGHT = 0;
		public static int PLAY_TYPE_PLAY_LIST = 1;
		public static int PLAY_TYPE_SIMPLE_PLAY_LIST = 2;
		
		public static String PLAYING_LIST_INFO_NAME = "PlayingListInfo";
		public static String PLAYING_LIST_INFO_MODE = "mode";
		public static String PLAYING_LIST_INFO_SUBMODE = "subMode";
		public static String PLAYING_LIST_INFO_PLAY_TYPE = "playType";
		public static String PLAYING_LIST_INFO_LIST_ID = "fileListId";
		public static String PLAYING_LIST_INFO_FILE_ID = "fileNameId";
		public static String PLAYING_LIST_INFO_COUNT = "count";
		public static String PLAYING_LIST_INFO_ID = "ID_";
		public static String PLAYING_LIST_INFO_BEGIN_POS="beginPos";
		
		public static final int WALLPAPER_NUM = 23;
		//public static int[] wallpaperResIds = new int[] { R.drawable.wallpaper1};
		
		public static int MAX_IGOO_VOLUME = 100;
		public static int MIN_IGOO_VOLUME = 0;
		public static int DEFAULT_IGOO_VOLUME = 70;
		
		public static int MAX_IGOO_LIGHT = 100;
		public static int MIN_IGOO_LIGHT = 0;
		public static int DEFAULT_IGOO_LIGHT = 100;
		
		public static int IGOO_STATUS_ICON_ID = 0;
		
	    public static int SONG_AND_LIGHT = 17;  
	    public static int ONLY_LIGHT = 1;
	    
	    public static final String ALARM_LIGHTSONG_NAME = "defaultAlarm.ils";
	
		public static final String BASE_INFO_FILENAME = "BaseInfo";
		public static final String BASE_INFO_VOLUME = "volume";
		public static final String BASE_INFO_LIGHT = "light";
		public static final String MAIN_PACKAGE_NAME = "com.igoo.launcher";
		public static final String BASE_INFO_SYS_VER = "sysVer";
		public static final String BASE_INFO_APP_VER = "appVer";
		public static final String BASE_INFO_LAMP_VER = "lampVer";
		
		public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
		
		public static String CONTROLLERS_ID_NAME_lIST = "ControllersIdNameList";
		public static String LAST_CONNECTED_SERVICE_IP = "LastConnectedServiceIp";
		public static String LAST_CONNECTED_CONTROLLER_ID = "LastConnectedControllerID";
	}
}
