package meekux.grandar.com.meekuxpjxroject.songdata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.io.File;
import java.util.ArrayList;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.utils.LogUtils;

public class GrandarDataBaseHelper extends SQLiteOpenHelper {
    private static final int modeCount = 9;
    private static final String TAG = "CreateDataBase";
    private static final String DATABASE_NAME = FileHelper.FILEPATH
            + "/sdcard_grandar.db";
    private static final int DATABASE_VERSION = 13;

    public class Tables {
        public static final String SUBMODE_IN_MODE = "submode_in_mode";
        public static final String TOTAL_SONGLIGHTS = "total_songlights";
        public static final String MODE1 = "mode1";
        public static final String MODE1_LIST_MEMBERS = "mode1_list_members";
        public static final String MODE2 = "mode2";
        public static final String MODE2_LIST_MEMBERS = "mode2_list_members";
        public static final String MODE3 = "mode3";
        public static final String MODE3_LIST_MEMBERS = "mode3_list_members";
        public static final String MODE4 = "mode4";
        public static final String MODE4_LIST_MEMBERS = "mode4_list_members";
        public static final String MODE5 = "mode5";
        public static final String MODE5_LIST_MEMBERS = "mode5_list_members";
        public static final String MODE6 = "mode6";
        public static final String MODE6_LIST_MEMBERS = "mode6_list_members";
        public static final String MODE7 = "mode7";
        public static final String MODE7_LIST_MEMBERS = "mode7_list_members";
        public static final String MODE8 = "mode8";
        public static final String MODE8_LIST_MEMBERS = "mode8_list_members";
        public static final String MODE9 = "mode9";
        public static final String MODE9_LIST_MEMBERS = "mode9_list_members";

        public static final String ALARMS = "alarms";

        public static final String NOTIFICATIONS = "notifications";

    }

    private Context mContext;

    public GrandarDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSDCardDatabase(db, mContext);
    }

    public static void createSDCardDatabase(SQLiteDatabase db, Context context) {
        /*
		 * File file = new File(DATABASE_NAME); if (file.exists()) {
		 * GrandarLogUtils.i(TAG, "createSDCardDatabase file.exists()"); //
		 * file.delete(); return; } SQLiteDatabase database =
		 * SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
		 */
		/* 创建每个模式对应的子模式表格 */
        createSubModeInModeTable(db, context);

		/* 创建包含所有光曲的总表表格 */
        createTotalSongLightsTable(db);

		/* 创建每个模式的播放列表表格 以及每个列表 对应的光曲列表表格 */
        for (int i = 1; i <= modeCount; i++) {
            String createTableSQL = "CREATE TABLE [mode" + i + "] ("
                    + "[_id] INTEGER,"
                    + "[name] TEXT,[date_added] LONG,[date_modified] LONG,"
                    + "[mode] INTEGER," + "[loop] BOOLEAN,"
                    + "[duration] LONG DEFAULT 0," + "PRIMARY KEY ([_id]));";
            long now = System.currentTimeMillis() / 1000;
            String createSubTableSQL = "CREATE TABLE [mode" + i
                    + "_list_members] (" + "[_id] INTEGER,"
                    + "[list_id] INTEGER," + "[songLight_id] INTEGER,"
                    + "[play_order] INTEGER," + "[name] TEXT,"
                    + "[mode] VARCHAR(20)," + "[submode] VARCHAR(20),"
                    + "[size] INTEGER," + "[checkcode] BINARY(16),"
                    + "[type] INTEGER," + "[duration] INTEGER DEFAULT 0,"
                    + "[times] LONG," + "PRIMARY KEY ([_id]));";

            String createDelListTriggerSQL = "CREATE TRIGGER mode" + i
                    + "_delete DELETE ON " + "mode" + i + " " + "BEGIN "
                    + "DELETE FROM " + "mode" + i + "_list_members"
                    + " WHERE list_id = OLD._id" + ";" + "END";
            // GrandarLogUtils.i(TAG, "createSDCardDatabase createSubTableSQL="+
            // createSubTableSQL);
            db.execSQL(createTableSQL);
			/*
			 * ContentValues contentValues = new ContentValues();
			 * contentValues.put("_id", 1); contentValues.put("name", "我的最爱");
			 * contentValues.put("date_added", now);
			 * contentValues.put("date_modified", now);
			 * contentValues.put("mode", i); if(i==2) {
			 * contentValues.put("loop", false); } else {
			 * contentValues.put("loop", true); } database.insert("mode" + i,
			 * null, contentValues);
			 */
            db.execSQL(createSubTableSQL);
            db.execSQL(createDelListTriggerSQL);
        }

        String createDelSongLightTriggerSQL = "CREATE TRIGGER total_songLight_delete DELETE ON "
                + "total_songlights"
                + " "
                + "BEGIN "
                + "DELETE FROM "
                + "mode1_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "DELETE FROM "
                + "mode2_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "DELETE FROM "
                + "mode3_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "DELETE FROM "
                + "mode4_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "DELETE FROM "
                + "mode5_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "DELETE FROM "
                + "mode6_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "DELETE FROM "
                + "mode7_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "DELETE FROM "
                + "mode8_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "DELETE FROM "
                + "mode9_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "END";
        db.execSQL(createDelSongLightTriggerSQL);

		/* 创建更新光曲总表中的 favor 字段时触发的触发器 */
        String createUpdateSongLightTriggerSQL = "CREATE TRIGGER total_songLight_update UPDATE OF "
                + SongLightStore.TotalSongLights.FAVOR
                + " ON "
                + "total_songlights"
                + " WHEN new."
                + SongLightStore.TotalSongLights.FAVOR + "=0 "
                + "BEGIN "
                + "DELETE FROM "
                + "mode9_list_members"
                + " WHERE songLight_id = OLD._id"
                + ";"
                + "END";
        db.execSQL(createUpdateSongLightTriggerSQL);

        createAlarmsTable(db);

        createNotificationsTable(db);

        createAppMsgNotifyTable(db);
        // db.close();
    }

    private static void createSubModeInModeTable(SQLiteDatabase database,
                                                 Context context) {
        // TODO Auto-generated method stub
        String createTableSQL = "CREATE TABLE [submode_in_mode] ("
                + "[_id] INTEGER," + "[name] TEXT," + "[mode] VARCHAR(20),"
                + "[submode] VARCHAR(20)," + "PRIMARY KEY ([_id]));";
        database.execSQL(createTableSQL);

        for (int i = 1; i <= modeCount; i++) {
			/* 根据模式得到子模式数组 */
            String[] modeNames = null;
            if (i == 1) {
                modeNames = context.getResources().getStringArray(
                        R.array.mode1_SubMode);
            } else if (i == 2) {
                modeNames = context.getResources().getStringArray(
                        R.array.mode2_SubMode);
            } else if (i == 3) {
                modeNames = context.getResources().getStringArray(
                        R.array.mode3_SubMode);
            } else if (i == 4) {
                modeNames = context.getResources().getStringArray(
                        R.array.mode4_SubMode);
            } else if (i == 5) {
                modeNames = context.getResources().getStringArray(
                        R.array.mode5_SubMode);
            } else if (i == 6) {
                modeNames = context.getResources().getStringArray(
                        R.array.mode6_SubMode);
            } else if (i == 7) {
                modeNames = context.getResources().getStringArray(
                        R.array.mode7_SubMode);
            } else if (i == 8) {
                modeNames = context.getResources().getStringArray(
                        R.array.mode8_SubMode);
            } else if (i == 9) {
                modeNames = context.getResources().getStringArray(
                        R.array.mode9_SubMode);
            }
            int subModeNum = modeNames.length;
            for (int j = 1; j <= subModeNum; j++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", modeNames[j - 1]);
                contentValues.put("mode", "mode" + i);
                contentValues.put("submode", "submode" + j);
                LogUtils.e(TAG, "createSubModeInModeTable "
                        + modeNames[j - 1] + " " + "mode" + i + " " + "submode"
                        + j);
                database.insert("submode_in_mode", null, contentValues);
            }
        }
    }

    private static void getFiles(ArrayList<File> fileList, String path) {
        File[] allFiles = new File(path).listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isFile()) {
                if (file.getAbsolutePath().contains("/mode")
				/* && (file.getAbsolutePath().contains("/submode")) */) {
                    // GrandarLogUtils.v(TAG, "getFiles name " +
                    // file.getName());
                    // if (file.getName().indexOf(".") >= 0) {
                    String fileS = file.getName().substring(
                            file.getName().length() - 4);
                    if (fileS.toLowerCase().equals(".ils")) {
                        fileList.add(file);
                    }

                    // }

                }
            } else {
                getFiles(fileList, file.getAbsolutePath());
            }
        }

    }

    private static void createTotalSongLightsTable(SQLiteDatabase database) {
        // TODO Auto-generated method stub
       // String fileDir = FileHelper.FILEPATH + "/";
        String createTableSQL = "CREATE TABLE [total_songlights] ("
                + "[_id] INTEGER," + "[name] TEXT," + "[mode] VARCHAR(20),"
                + "[submode] VARCHAR(20)," + "[size] INTEGER,"
                + "[checkcode] BINARY(16)," + "[type] INTEGER,"
                + "[favor] BOOLEAN," + "[author] VARCHAR(30),"
                + "[duration] INTEGER DEFAULT 0,"
                + "[simplelist] BOOLEAN DEFAULT 1,"
                + "[favor_simplelist] BOOLEAN DEFAULT 1,"
                + "[new] BOOLEAN DEFAULT 0,"
                + "[describe] TEXT DEFAULT NULL,"  //光曲描述
                + "[icon] blob DEFAULT NULL,"  //光曲图标
                + "[servers_id] INTEGER DEFAULT -1," //光曲商店网站服务器上对应的id 下载的才会有
                + "[download_id] LONG  DEFAULT -1," //从光曲商店下载时 downloadmanager数据库中的id 下载的才会有
                + "[songlight_verno] INTEGER DEFAULT 0," //光曲文件里面内置的版本号信息
                + "PRIMARY KEY ([_id]));";

        database.execSQL(createTableSQL);


    }

    private static void createAppMsgNotifyTable(SQLiteDatabase database) {
        String createTableSQL = "CREATE TABLE [apps_msg_notify] (" + "[_id] INTEGER,"
                + "[package_name] TEXT," + "[msg_count] INTEGER,"
                + "PRIMARY KEY ([_id]));";
        database.execSQL(createTableSQL);
    }


    private static void createAlarmsTable(SQLiteDatabase database) {
        // TODO Auto-generated method stub
        String createTableSQL = "CREATE TABLE [alarms] (" + "[_id] INTEGER,"
                + "[hour] INTEGER," + "[minutes] INTEGER,"
                + "[daysofweek] INTEGER," + "[alarmtime] INTEGER,"
                + "[enabled] BOOLEAN," + "[lightoff] BOOLEAN,"
                + "[message] TEXT," + "[mode] INTEGER,"
                + "[play_type] INTEGER," + "[play_id] INTEGER,"
                + "[snooze] BOOLEAN,"
                + "[set_bright_volume] BOOLEAN,"
                + "[bright] INTEGER," + "[volume] INTEGER,"
                + "PRIMARY KEY ([_id]));";

        database.execSQL(createTableSQL);
    }

    private static void createNotificationsTable(SQLiteDatabase database) {
        // TODO Auto-generated method stub
        String createTableSQL = "CREATE TABLE [notifications] ("
                + "[_id] INTEGER," + "[showed] BOOLEAN," + "[read] BOOLEAN,"
                + "[time]  LONG," + "[title] TEXT," + "[message] TEXT,"
                + "[title_en] TEXT," + "[message_en] TEXT,"
                + "[uri] TEXT,"
                + "[from_domain] TEXT,"
                + "[packet_id] TEXT,"
                + "PRIMARY KEY ([_id]));";

        database.execSQL(createTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        LogUtils.e(TAG, "Upgrading Grandar database from version "
                + oldVersion + " to " + newVersion);
        if (oldVersion < 4) {
            LogUtils.e(TAG, "which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.SUBMODE_IN_MODE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TOTAL_SONGLIGHTS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE1 + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE1_LIST_MEMBERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE2 + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE2_LIST_MEMBERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE3 + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE3_LIST_MEMBERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE4 + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE4_LIST_MEMBERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE5 + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE5_LIST_MEMBERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE6 + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE6_LIST_MEMBERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE7 + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE7_LIST_MEMBERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE8 + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE8_LIST_MEMBERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE9 + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MODE9_LIST_MEMBERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.ALARMS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.NOTIFICATIONS + ";");
            onCreate(db);
            return;
        }

        if (oldVersion <= 4) {
            db.execSQL("ALTER TABLE total_songlights ADD COLUMN new BOOLEAN DEFAULT 0;");
        }

        if (oldVersion <= 5) {
            LogUtils.e(TAG, "which will destroy Tables.SUBMODE_IN_MODE old data");
            db.execSQL("DROP TABLE IF EXISTS " + Tables.SUBMODE_IN_MODE + ";");
            createSubModeInModeTable(db, mContext);
            createAppMsgNotifyTable(db);
        }

        if (oldVersion <= 6) {
            LogUtils.e(TAG, "onUpgrade oldVersion<=6");
            db.execSQL("ALTER TABLE total_songlights ADD COLUMN describe TEXT DEFAULT NULL;");
            db.execSQL("ALTER TABLE total_songlights ADD COLUMN icon BLOB DEFAULT NULL;");

            //将默认闹铃光曲信息插入到数据库中
            File file = new File(FileHelper.FILEPATH + "/mode11", Constant.ConValue.ALARM_LIGHTSONG_NAME);
            if (!file.exists()) {
                FileHelper.copyAssetsDataToSD(mContext, FileHelper.FILEPATH + "/mode11", Constant.ConValue.ALARM_LIGHTSONG_NAME);
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put("name", file.getName());
            contentValues.put("mode", "mode11");
            contentValues.put("checkcode",
                    MyUtil.getSongLightMd5(file.getAbsolutePath()));
            contentValues.put("size", file.length());
            contentValues.put("type",
                    MyUtil.getSongLightType(file.getAbsolutePath()));
            contentValues.put("favor", false);
            contentValues.put("duration",
                    MyUtil.getSongLightTime(file.getAbsolutePath()));
            contentValues.put("describe", MyUtil.getSongLightDescr(file.getAbsolutePath()));
            contentValues.put("icon", MyUtil.getSongLightIcon(file.getAbsolutePath()));
            db.insert("total_songlights", null, contentValues);
        }

        if (oldVersion <= 7) {
            LogUtils.e(TAG, "onUpgrade oldVersion<=7");
            db.execSQL("ALTER TABLE total_songlights ADD COLUMN servers_id INTEGER DEFAULT -1;");
            db.execSQL("ALTER TABLE total_songlights ADD COLUMN download_id LONG DEFAULT -1;");
        }
        if (oldVersion <= 8) {
            LogUtils.e(TAG, "onUpgrade oldVersion<=8");
            db.execSQL("ALTER TABLE total_songlights ADD COLUMN songlight_verno LONG DEFAULT 0;");
        }
        if (oldVersion <= 9) {
            LogUtils.e(TAG, "onUpgrade oldVersion<=9");
            db.execSQL("ALTER TABLE alarms ADD COLUMN snooze BOOLEAN DEFAULT 0;");
        }
        if (oldVersion <= 10) {
            LogUtils.e(TAG, "onUpgrade oldVersion<=10");
            db.execSQL("ALTER TABLE notifications ADD COLUMN title_en TEXT DEFAULT NULL;");
            db.execSQL("ALTER TABLE notifications ADD COLUMN message_en TEXT DEFAULT NULL;");
        }
        if (oldVersion <= 11) {
            LogUtils.e(TAG, "onUpgrade oldVersion<=11");
            db.execSQL("ALTER TABLE notifications ADD COLUMN from_domain TEXT DEFAULT NULL;");
            db.execSQL("ALTER TABLE notifications ADD COLUMN packet_id TEXT DEFAULT NULL;");
        }
        if (oldVersion <= 12) {
            LogUtils.e(TAG, "onUpgrade oldVersion<=12");
            db.execSQL("ALTER TABLE alarms ADD COLUMN set_bright_volume TEXT DEFAULT NULL;");
            db.execSQL("ALTER TABLE alarms ADD COLUMN bright INTEGER DEFAULT " + Constant.ConValue.DEFAULT_IGOO_LIGHT + ";");
            db.execSQL("ALTER TABLE alarms ADD COLUMN volume INTEGER DEFAULT " + Constant.ConValue.DEFAULT_IGOO_VOLUME + ";");
        }
    }
}
