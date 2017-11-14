package meekux.grandar.com.meekuxpjxroject.songdata.contentprovide;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.socks.library.KLog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import meekux.grandar.com.meekuxpjxroject.songdata.database.FileHelper;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarDataBaseHelper;
import meekux.grandar.com.meekuxpjxroject.songdata.database.GrandarUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.SongLightStore;
import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;
import meekux.grandar.com.meekuxpjxroject.songdata.database.SongLightStore.Playlists.Members;

public class SongLightContentProvider extends ContentProvider {

    private static final String TAG = "SongLightContentProvider";
    private static UriMatcher uriMatcher;
    private static final String AUTHORITY = "com.grandar.igoo.igooproject.songlightcontentprovider";
    private static final int LISTS = 1; // 查询该模式下所有的列表 信息
    private static final int SUBMODE = 2; // 该模式下的所有子模式
    private static final int TOTAL_SONG_LIGHT = 3; // 所有光曲
    private static final int PLAYLIST_MEMBERS = 4; // 播放列表中的光曲
    private static final int MOVE_PLAYLIST_MEMBERS = 5; // 改变播放列表中的光曲的顺序
    private static final int ALARMS = 6; // 灯光定时中所有的定时
    private static final int ALARMS_ID = 7; // 灯光定时中对应id的定时
    private static final int TOTAL_SONGLIGHTS_ID = 8; // 光曲总表中对应id的光曲
    private static final int NOTIFICATIONS = 9; // 数据库中的所有推送消息
    private static final int NOTIFICATIONS_ID = 10; // 数据库中对应ID的推送消息
    private static final int PLAYLIST_MEMBERS_JOIN_TOTAL_SONGLIGHTS = 11; //播放列表中

    static final String TABLE_PLAYLIST_MEMBERS = "playlist_members";
    static final HashMap<String, String> PLAYLIST_MEMBERS_PROJECTION_MAP = new HashMap<String, String>();

    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "mode/#", LISTS);
        uriMatcher.addURI(AUTHORITY, "submode_in_mode/", SUBMODE);
        uriMatcher.addURI(AUTHORITY, "total_songlights/", TOTAL_SONG_LIGHT);
        uriMatcher.addURI(AUTHORITY, "members/*", PLAYLIST_MEMBERS);
        uriMatcher.addURI(AUTHORITY, "members/*/*", MOVE_PLAYLIST_MEMBERS);
        uriMatcher.addURI(AUTHORITY, "alarms/", ALARMS);
        uriMatcher.addURI(AUTHORITY, "alarms/#", ALARMS_ID);
        uriMatcher.addURI(AUTHORITY, "total_songlights/#", TOTAL_SONGLIGHTS_ID);
        uriMatcher.addURI(AUTHORITY, "notifications/", NOTIFICATIONS);
        uriMatcher.addURI(AUTHORITY, "notifications/#", NOTIFICATIONS_ID);
        uriMatcher.addURI(AUTHORITY, "join/members/*", PLAYLIST_MEMBERS_JOIN_TOTAL_SONGLIGHTS);

        HashMap<String, String> map;

        map = PLAYLIST_MEMBERS_PROJECTION_MAP;
        map.put(Members._ID, "Members._id");
        map.put(Members.LIST_ID, "Members.list_id");
        map.put(Members.SONGLIGHT_ID, "Members.songLight_id");
        map.put(Members.PLAY_ORDER, "Members.play_order");
        map.put(Members.NAME, "Members.name");
        map.put(Members.MODE, "Members.mode");
        map.put(Members.SUBMODE, "Members.submode");
        map.put(Members.SIZE, "Members.size");
        map.put(Members.TIMES, "Members.times");
        map.put(Members.DURATION, "Members.duration");
        map.put(Members.TYPE, "Members.type");
        map.put(Members.CHECKCODE, "Members.checkcode");
        map.put(Members.DESCRIBE, "Total.describe");
        map.put(Members.ICON, "Total.icon");
    }

    static final String qualifyColumn(String table, String column) {
        return table + "." + column + " AS " + column;
    }

    public static GrandarDataBaseHelper mDatabaseHelper;


    @Override
    public int delete(Uri uri, String userWhere, String[] whereArgs) {
        int count = -1;
        String table = null;
        String where = null;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case TOTAL_SONG_LIGHT:
                table = uri.getPathSegments().get(0);
                // 根据id 查询路径名
                String[] cols = new String[]{SongLightStore.TotalSongLights.NAME,
                        SongLightStore.TotalSongLights.MODE,
                        SongLightStore.TotalSongLights.SUBMODE};
                Cursor c = query(uri, cols, userWhere, whereArgs, null);
                String filePath = null;
                String fileName = null;
                if (c != null) {
                    c.moveToFirst();
                    filePath = FileHelper.FILEPATH
                            + "/"
                            + c.getString(c
                            .getColumnIndexOrThrow(SongLightStore.TotalSongLights.MODE));
                    if (c.getString(c
                            .getColumnIndexOrThrow(SongLightStore.TotalSongLights.SUBMODE)) != null) {
                        filePath = filePath
                                + "/"
                                + c.getString(c
                                .getColumnIndexOrThrow(SongLightStore.TotalSongLights.SUBMODE));
                    }
                    fileName = c
                            .getString(c
                                    .getColumnIndexOrThrow(SongLightStore.TotalSongLights.NAME));
                    c.close();
                }

                count = db.delete(table, userWhere, whereArgs);
                if (count > 0) {
                    FileHelper.deleteDiskFile(filePath, fileName);
                }
                break;
            case LISTS:
                table = "mode" + uri.getPathSegments().get(1);

                // String selectSQL = "select name, data_added from " + table +
                // where;
                // Cursor cursor = database.rawQuery(selectSQL, whereArgs);
                // cursor.moveToFirst();
                // String subTable =table+"_list_members";
                // where = SongLightStore.Playlists.Members.LIST_ID+"= ?";
                // GrandarLogUtils.i(TAG, "delete subTable=" + subTable);
                // database.delete(subTable,where,whereArgs);

                count = db.delete(table, userWhere, whereArgs);
                break;

            case PLAYLIST_MEMBERS:
                table = uri.getPathSegments().get(1);
                count = db.delete(table, userWhere, whereArgs);
                break;
            case ALARMS:
                table = uri.getPathSegments().get(0);
                count = db.delete(table, userWhere, whereArgs);
                break;
            case ALARMS_ID:
                table = uri.getPathSegments().get(0);
                where = "_id=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(userWhere)) {
                    where = where + " AND (" + userWhere + ")";
                }
                count = db.delete(table, where, whereArgs);
                break;
            case NOTIFICATIONS:
                table = uri.getPathSegments().get(0);
                count = db.delete(table, userWhere, whereArgs);
                break;
            case NOTIFICATIONS_ID:
                table = uri.getPathSegments().get(0);
                where = "_id=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(userWhere)) {
                    where = where + " AND (" + userWhere + ")";
                }
                count = db.delete(table, where, whereArgs);
                break;
            default:
                return 0;
        }
        LogUtils.e(TAG, "delete count=" + count);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Long now = Long.valueOf(System.currentTimeMillis() / 1000);
        String table = null, name = null;
        long id;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case TOTAL_SONG_LIGHT:
                table = uri.getPathSegments().get(0);
                name = null;
                id = db.insert(table, name, values);
                break;
            case LISTS:

                table = "mode" + uri.getPathSegments().get(1);
                if (values.containsKey("name") == false) {
                    values.put("name", "");
                }
                if (values.containsKey(SongLightStore.Playlists.DATE_ADDED) == false) {
                    values.put(SongLightStore.Playlists.DATE_ADDED, now);
                }
                if (values.containsKey(SongLightStore.Playlists.DATE_MODIFIED) == false) {
                    values.put(SongLightStore.Playlists.DATE_MODIFIED, now);
                }

                name = "name";

                id = db.insert(table, name, values);

                break;

            case PLAYLIST_MEMBERS:
                table = uri.getPathSegments().get(1);
                name = null;
                id = db.insert(table, name, values);
                break;
            case ALARMS:
                table = uri.getPathSegments().get(0);
                name = null;
                id = db.insert(table, name, values);
                break;
            case NOTIFICATIONS:
                table = uri.getPathSegments().get(0);
                name = null;
                id = db.insert(table, name, values);
                break;
            default:
                return uri;
        }

        if (id > 0) {
            Uri insertedUserUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(insertedUserUri,
                    null);
            LogUtils.e(TAG, "insert table=" + table + " name=" + name
                    + " id=" + id + " Uri=" + insertedUserUri);
            return insertedUserUri;
        } else {
            return uri;
        }

    }


    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub

        KLog.e("内容提供者开始创建---->>");
        /* 判断是否存在版本号不为1的老数据库，存在则直接删除 */
        if (true == Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File file = new File(FileHelper.FILEPATH + "/sdcard_grandar.db");
            if (file.exists()) {
                SQLiteDatabase database = SQLiteDatabase.openDatabase(
                        FileHelper.FILEPATH + "/sdcard_grandar.db", null,
                        SQLiteDatabase.OPEN_READONLY);
                try {
                    LogUtils.e(TAG, "onCreate database.getVersion():"
                            + database.getVersion());
                    if (database.getVersion() == 0) {
                        file.delete();
                    }
                } finally {
                    database.close();
                }
            }
            mDatabaseHelper = new GrandarDataBaseHelper(getContext());
            GrandarUtils.setExternalStorageState(true, getContext());
            KLog.e("表创建完成----------->>");
        }

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        String table = null;
        String where = null;
        LogUtils.e(TAG, "query uri=" + uri + " selection=" + selection
                + " selectionArgs=" + selectionArgs + " sortOrder="
                + sortOrder);
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case LISTS:
                String modeName = "mode" + uri.getPathSegments().get(1);
                LogUtils.e(TAG, "query LISTS modeName=" + modeName);
                cursor = db.query(modeName, projection, selection, selectionArgs,
                        null, null, sortOrder);
                LogUtils.e(TAG, "query cursor modeName=" + cursor);
                break;

            case SUBMODE:
                cursor = db.query("submode_in_mode", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TOTAL_SONG_LIGHT:
                cursor = db.query("total_songlights", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PLAYLIST_MEMBERS:
                String tableName = uri.getPathSegments().get(1);
                LogUtils.e(TAG, "query LISTS modeName=" + tableName);
                cursor = db.query(tableName, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PLAYLIST_MEMBERS_JOIN_TOTAL_SONGLIGHTS:
                SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
                String jTableName = uri.getPathSegments().get(2);
                LogUtils.e(TAG, "query LISTS modeName=" + jTableName);
                String TABLE_LIST_MEMBERS_JOIN_TOTAL_SONGLIGHTS = jTableName + " As Members LEFT OUTER JOIN total_songlights AS Total " +
                        "ON Members.songLight_id = Total._id";
                qb.setProjectionMap(PLAYLIST_MEMBERS_PROJECTION_MAP);
                qb.setTables(TABLE_LIST_MEMBERS_JOIN_TOTAL_SONGLIGHTS);
                cursor = qb.query(db, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ALARMS:
                cursor = db.query("alarms", projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ALARMS_ID:
                table = uri.getPathSegments().get(0);
                where = "_id=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(selection)) {
                    where = where + " AND (" + selection + ")";
                }
                cursor = db.query(table, projection, where, selectionArgs, null,
                        null, sortOrder);
                break;
            case TOTAL_SONGLIGHTS_ID:
                table = uri.getPathSegments().get(0);
                where = "_id=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(selection)) {
                    where = where + " AND (" + selection + ")";
                }
                cursor = db.query(table, projection, where, selectionArgs, null,
                        null, sortOrder);
                break;
            case NOTIFICATIONS:
                LogUtils.e(TAG, "query NOTIFICATIONS table=" + table
                        + " projection=" + projection[0].toString() + " selection="
                        + selection + " selectionArgs=" + selectionArgs
                        + " sortOrder=" + sortOrder);
                cursor = db.query("notifications", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case NOTIFICATIONS_ID:
                table = uri.getPathSegments().get(0);
                where = "_id=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(selection)) {
                    where = where + " AND (" + selection + ")";
                }
                cursor = db.query(table, projection, where, selectionArgs, null,
                        null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("<" + uri + ">格式不正确.");
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String userWhere,
                      String[] whereArgs) {
        int count = -1;
        String table = null;
        String where = null;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case LISTS:
                table = "mode" + uri.getPathSegments().get(1);
                count = db.update(table, values, userWhere, whereArgs);
                break;

            case PLAYLIST_MEMBERS:
                table = uri.getPathSegments().get(1);
                count = db.update(table, values, userWhere, whereArgs);
                break;
            case MOVE_PLAYLIST_MEMBERS:
                String moveit = uri.getQueryParameter("move");
                if (moveit != null) {
                    String key = Members.PLAY_ORDER;
                    if (values.containsKey(key)) {
                        int newpos = values.getAsInteger(key);
                        List<String> segments = uri.getPathSegments();
                        table = segments.get(1);
                        // Long id = Long.valueOf(segments.get(2));
                        int oldpos = Integer.valueOf(segments.get(2));
                        int playId = values
                                .getAsInteger(Members.LIST_ID);
                        return movePlaylistEntry(db, table, oldpos, newpos, playId);
                    }
                    throw new IllegalArgumentException("Need to specify " + key
                            + " when using 'move' parameter");
                }
                break;
            case ALARMS:
                table = uri.getPathSegments().get(0);
                count = db.update(table, values, userWhere, whereArgs);
                break;
            case ALARMS_ID:
                table = uri.getPathSegments().get(0);
                where = "_id=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(userWhere)) {
                    where = where + " AND (" + userWhere + ")";
                }
                count = db.update(table, values, where, whereArgs);
                break;
            case TOTAL_SONG_LIGHT:
                table = uri.getPathSegments().get(0);
                count = db.update(table, values, userWhere, whereArgs);
                break;
            case TOTAL_SONGLIGHTS_ID:
                table = uri.getPathSegments().get(0);
                where = "_id=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(userWhere)) {
                    where = where + " AND (" + userWhere + ")";
                }
                count = db.update(table, values, where, whereArgs);
                break;
            case NOTIFICATIONS:
                table = uri.getPathSegments().get(0);
                count = db.update(table, values, userWhere, whereArgs);
                break;
            case NOTIFICATIONS_ID:
                table = uri.getPathSegments().get(0);
                where = "_id=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(userWhere)) {
                    where = where + " AND (" + userWhere + ")";
                }
                count = db.update(table, values, where, whereArgs);
                break;
            default:
                return 0;
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numValues = values.length;

        for (int i = 0; i < numValues; i++) {
            insert(uri, values[i]);
        }

        return numValues;
    }

    private int movePlaylistEntry(SQLiteDatabase db, String tableName,
                                  int from, int to, int playId) {
        if (from == to) {
            return 0;
        }
        db.beginTransaction();
        try {
            int numlines = 0;
            db.execSQL("UPDATE " + tableName + " SET play_order=-1"
                    + " WHERE list_id=" + playId + " AND" + " play_order="
                    + from);
            // We could just run both of the next two statements, but only one
            // of
            // of them will actually do anything, so might as well skip the
            // compile
            // and execute steps.
            if (from < to) {
                db.execSQL("UPDATE " + tableName
                        + " SET play_order=play_order-1" + " WHERE list_id="
                        + playId + " AND" + " play_order<=" + to
                        + " AND play_order>" + from);
                numlines = to - from + 1;
            } else {
                db.execSQL("UPDATE " + tableName
                        + " SET play_order=play_order+1" + " WHERE list_id="
                        + playId + " AND" + " play_order>=" + to
                        + " AND play_order<" + from);
                numlines = from - to + 1;
            }
            db.execSQL("UPDATE " + tableName + " SET play_order=" + to
                    + " WHERE list_id=" + playId + " AND" + " play_order=-1");
            db.setTransactionSuccessful();
            Uri uri = Members.getContentUri(tableName)
                    .buildUpon().appendEncodedPath(String.valueOf(playId))
                    .build().buildUpon().appendEncodedPath("movePlayList")
                    .build();
            // GrandarLogUtils.d(TAG, "movePlaylistEntry uri:" + uri);
            getContext().getContentResolver().notifyChange(uri, null);
            return numlines;
        } finally {
            db.endTransaction();
        }
    }
}
