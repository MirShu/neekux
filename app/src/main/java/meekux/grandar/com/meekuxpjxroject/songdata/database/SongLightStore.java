package meekux.grandar.com.meekuxpjxroject.songdata.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

public class SongLightStore {
	private final static String TAG = "SongLightStore";

	public static final String AUTHORITY = "com.grandar.igoo.igooproject.songlightcontentprovider";
	private static final String CONTENT_AUTHORITY_SLASH = "content://" + AUTHORITY + "/";

	/**
	 * Columns representing a playlist
	 */
	public interface TotalSongLightsColumns {
		/**
		 * The name of the playlist
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * The data stream for the playlist file
		 * <P>
		 * Type: DATA STREAM
		 * </P>
		 */
		public static final String MODE = "mode";

		/**
		 * The time the file was added to the media provider Units are seconds
		 * since 1970.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String SUBMODE = "submode";
		
		public static final String SIZE = "size";
		
		public static final String CHECKCODE = "checkcode";
		
		public static final String TYPE = "type";

		public static final String FAVOR = "favor";
		
		public static final String AUTHOR = "author";
	
		public static final String DURATION = "duration";
		
		public static final String SIMPLELIST = "simplelist";
		
		public static final String FAVOR_SIMPLELIST = "favor_simplelist";
		
		public static final String NEW_FLAG = "new";
		
		public static final String DESCRIBE = "describe";
		
		public static final String ICON = "icon";
		
		public static final String SERVERS_ID = "servers_id";
		
		public static final String DOWNLOAD_ID = "download_id";
		
		public static final String SONGLIGHT_VERNO="songlight_verno";
	}

	/**
	 * Contains playlists for audio files
	 */
	public static final class TotalSongLights implements BaseColumns,
	TotalSongLightsColumns {
		/**
		 * Get the content:// style URI for the audio playlists table on the
		 * given volume.
		 * 
		 *            the name of the volume to get the URI for
		 * @return the URI to the audio playlists table on the given volume
		 */
		public static Uri getContentUri() {
			return Uri.parse(CONTENT_AUTHORITY_SLASH + "total_songlights/");
		}

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = NAME;

	}
	/**
	 * Columns representing a playlist
	 */
	public interface SubModeInModeColumns {
		/**
		 * The name of the playlist
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * The data stream for the playlist file
		 * <P>
		 * Type: DATA STREAM
		 * </P>
		 */
		// public static final String DATA = "_data";

		/**
		 * The time the file was added to the media provider Units are seconds
		 * since 1970.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String MODE = "mode";
		
		public static final String SUBMODE = "submode";
	}

	/**
	 * Contains playlists for audio files
	 */
	public static final class SubModeInMode implements BaseColumns,
			SubModeInModeColumns {
		/**
		 * Get the content:// style URI for the audio playlists table on the
		 * given volume.
		 * 
		 *            the name of the volume to get the URI for
		 * @return the URI to the audio playlists table on the given volume
		 */
		public static Uri getContentUri() {
			return Uri.parse(CONTENT_AUTHORITY_SLASH + "submode_in_mode/");
		}

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = _ID;

	}

	/**
	 * @hide
	 */
	public interface PlaylistsExtensionColumns {
		/**
		 * A pinyin key calculated from the NAME, used for sorting and grouping
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME_PINYIN_KEY = "name_pinyin_key";
	}

	/**
	 * Columns representing a playlist
	 */
	public interface PlaylistsColumns extends PlaylistsExtensionColumns {
		/**
		 * The name of the playlist
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * The data stream for the playlist file
		 * <P>
		 * Type: DATA STREAM
		 * </P>
		 */
		// public static final String DATA = "_data";

		/**
		 * The time the file was added to the media provider Units are seconds
		 * since 1970.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String DATE_ADDED = "date_added";

		/**
		 * The time the file was last modified Units are seconds since 1970.
		 * NOTE: This is for internal use by the media scanner. Do not modify
		 * this field.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String DATE_MODIFIED = "date_modified";
		
		public static final String MODE = "mode";
		
		public static final String LOOP = "loop";
		
		public static final String DURATION = "duration";
	}

	/**
	 * Contains playlists for audio files
	 */
	public static final class Playlists implements BaseColumns,
			PlaylistsColumns {
		/**
		 * Get the content:// style URI for the audio playlists table on the
		 * given volume.
		 * 
		 *            the name of the volume to get the URI for
		 * @return the URI to the audio playlists table on the given volume
		 */
		public static Uri getContentUri(String modeIdx) {
			return Uri.parse(CONTENT_AUTHORITY_SLASH + "mode/" + modeIdx);
		}

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = NAME;
        /**
         * Sub-directory of each playlist containing all members.
         */
        public static final class Members implements BaseColumns,TotalSongLightsColumns{
            public static final Uri getContentUri(String tableName) {
                return Uri.parse(CONTENT_AUTHORITY_SLASH +"members/"+ tableName);
            }

            public static final Uri getJoinContentUri(String tableName) {
                return Uri.parse(CONTENT_AUTHORITY_SLASH +"join/members/"+ tableName);
            }
            /**
             * Convenience method to move a playlist item to a new location
             * @param res The content resolver to use
             * @param from The position of the item to move
             * @param to The position to move the item to
             * @return true on success
             */
            public static final boolean moveItem(ContentResolver res,
                    String tableName,int from, int to,int listId) {
                Uri uri = Members.getContentUri(tableName)
                        .buildUpon()
                        .appendEncodedPath(String.valueOf(from))
                        .appendQueryParameter("move", "true")
                        .build();
                ContentValues values = new ContentValues();
                values.put( Members.PLAY_ORDER, to);
				values.put( Members.LIST_ID, listId);
                return res.update(uri, values, null, null) != 0;
            }


            /**
             * A subdirectory of each playlist containing all member audio
             * files.
             */
            public static final String LIST_ID = "list_id";
            
            public static final String SONGLIGHT_ID = "songLight_id";

            /**
             * The order of the songs in the playlist
             * <P>Type: INTEGER (long)></P>
             */
            public static final String PLAY_ORDER = "play_order";
            
    		public static final String TIMES = "times";
            /**
             * The default sort order for this table
             */
            public static final String DEFAULT_SORT_ORDER = PLAY_ORDER;
        }
	}

	/**
	 * Columns representing a playlist
	 */
	public interface NotificationColumns {
		public static final String SHOWED = "showed";

		public static final String READ = "read";

		public static final String TIME = "time";
		
		public static final String TITLE = "title";
		
		public static final String MESSAGE = "message";
		
		public static final String URI = "uri";
		
		public static final String DEFAULT_SORT_ORDER = TIME+" DESC";
		
		public static final String TITLE_EN = "title_en";
		
		public static final String MESSAGE_EN = "message_en";
		  
		public static final String FROM = "from_domain";
		
		public static final String PACKET_ID = "packet_id";
	}

	/**
	 * Contains playlists for audio files
	 */
	public static final class Notifications implements BaseColumns,
	NotificationColumns {
		/**
		 * Get the content:// style URI for the audio playlists table on the
		 * given volume.
		 * 
		 *            the name of the volume to get the URI for
		 * @return the URI to the audio playlists table on the given volume
		 */
		public static Uri getContentUri() {
			return Uri.parse(CONTENT_AUTHORITY_SLASH + "notifications/");
		}

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = TIME;

        static final String[] NOTIFI_QUERY_COLUMNS = {
            _ID, SHOWED, READ, TIME, TITLE,
            MESSAGE, URI};
	}


}
