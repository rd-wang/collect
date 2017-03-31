package com.master.app.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.master.app.Constants;
import com.master.app.SynopsisObj;
import com.master.app.manager.ConfigManager;
import com.master.app.manager.RecordingMedium;
import com.master.app.tools.LoggerUtils;
import com.master.app.tools.PreferencesUtils;
import com.master.bean.Fields;
import com.master.bean.LdData;
import com.master.bean.LocaDate;
import com.master.bean.Maps;
import com.master.bean.Point;
import com.master.bean.TableContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/6
 */
public class DbHelperDbHelper {

    private static final String TAG = "DbHelper";

    private static final String DATABASE_NAME = "DbHelper.db";

    private static final int DATABASE_VERSION = 1;

    private static DbHelperDbHelper dbHelperDbHelper = new DbHelperDbHelper(
            SynopsisObj.getAppContext());

    protected static SQLiteDatabase mDb;

    private static DbHelper mDbHelper;


    public DbHelperDbHelper(Context context) {

        mDbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
            @Override
            protected void execSqlTable(SQLiteDatabase db) {
                LoggerUtils.d("sql_T_MAP", sql_T_MAP);
                db.execSQL(sql_T_MAP);

                List<String> sqls = getsAttributeTableSQL();

                db.execSQL(sql_T_LX);
                LoggerUtils.d("sql_T_LX", sql_T_LX);

                for (String s : sqls) {
                    db.execSQL(s);
                    LoggerUtils.d("sql_T_JWD", s);
                }
            }
        };
    }

    public void closeDb() {

        if (mDb != null) {
            mDb.close();
        }
    }

    public static DbHelperDbHelper open() throws SQLException {
        if (mDb != null) {
            mDb.close();
        }
        mDb = mDbHelper.getWritableDatabase();
        return dbHelperDbHelper;
    }

    public DbHelperDbHelper beginTransation() {
        mDb.beginTransaction();
        return dbHelperDbHelper;
    }

    public void endTransaction() {
        if (mDb != null) {
            mDb.endTransaction();
        }
        closeDb();
    }

    public void setTransactionSuccessful() {
        mDb.setTransactionSuccessful();

    }

    public DbHelperDbHelper endTransation() {
        mDb.setTransactionSuccessful();
        return dbHelperDbHelper;
    }


    public void closeTransation() {
        mDb.endTransaction();
    }


    // -------------- T_MAP DEFINITIONS ------------
    public static final String MAP_TABLE = "T_MAP";

    public static final String MAP_NUMBER_COLUMN = "MAPID";

    public static final int MAP_MAPID_COLUMN_POSITION = 1;

    public static final String MAP_MAPNAME_COLUMN = "MAPNAME";

    public static final int MAP_MAPNAME_COLUMN_POSITION = 2;

    public static final String MAP_CJSJ_COLUMN = "CJSJ";

    public static final int MAP_CJSJ_COLUMN_POSITION = 3;

    // 是否当前使用，填“是”或“否” 有且只有一个工作地图为“是”
    public static final String MAP_SFDQSY_COLUMN = "SFDQSY";

    public static final int MAP_SFDQSY_COLUMN_POSITION = 4;

    // -------------- T_JWD DEFINITIONS ------------


    public static final String JWD_CROWID_COLUMN = "CROWID";

    public static final int JWD_CROWID_COLUMN_POSITION = 1;

    public static final String JWD_FID_COLUMN = "FID";

    public static final int JWD_FID_COLUMN_POSITION = 2;

    //区分采集对象，是乡镇还是路线
    public static final String JWD_CJDX_COLUMN = "CJDX";

    public static final int JWD_CJDX_COLUMN_POSITION = 3;

    public static final String JWD_JD_COLUMN = "JD";

    public static final int JWD_JD_COLUMN_POSITION = 4;

    public static final String JWD_WD_COLUMN = "WD";

    public static final int JWD_WD_COLUMN_POSITION = 5;

    public static final String JWD_CJSJ_COLUMN = "CJSJ";

    public static final int JWD_CJSJ_COLUMN_POSITION = 6;


    private static final String LX_TABLE = "T_LX";

    private static final String LX_LXBM = "LXBM";

    private static final String LX_LXMC = "LXMC";

    private static String sql_T_LX = "CREATE TABLE " + LX_TABLE + " (" +
            JWD_CROWID_COLUMN + " INTEGER  PRIMARY KEY AUTOINCREMENT," +
            LX_LXBM + " varchar(50) NOT NULL," +
            LX_LXMC + " varchar(500)" +
            ")";


    private static String sql_T_MAP = "CREATE TABLE " + MAP_TABLE + " (" +
            MAP_NUMBER_COLUMN + " INTEGER  PRIMARY KEY AUTOINCREMENT," +
            MAP_MAPNAME_COLUMN + " varchar(50) NOT NULL," +
            MAP_CJSJ_COLUMN + " DATE NOT NULL," +
            MAP_SFDQSY_COLUMN + " varchar(10)" +
            ")";


    //    // ----------------MAP HELPERS --------------------
    public int addMAp(String mapName, Date cjsj, String sfdqsy) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MAP_MAPNAME_COLUMN, mapName);
        contentValues.put(MAP_CJSJ_COLUMN, cjsj.getTime());
        contentValues.put(MAP_SFDQSY_COLUMN, sfdqsy);
        mDb.insert(MAP_TABLE, null, contentValues);

        String sql = "select last_insert_rowid() from " + MAP_TABLE;
        Cursor cursor = mDb.rawQuery(sql, null);
        int a = -1;
        if (cursor.moveToFirst()) {
            a = cursor.getInt(0);
        }

        cursor.close();
        return a;

    }

    /**
     * 保存点对象,
     *
     * @param tableName 需要插入的表
     * @param param     参数
     * @param userMapid 是否添加当前mapid
     */
    public long addTablEntry(String tableName, Map<String, String> param, Boolean userMapid) {
        ContentValues contentValues = new ContentValues();
        Set<String> strings = param.keySet();
        for (String s : strings) {
            String s1s = param.get(s);
            contentValues.put(s, s1s);
        }
        if (userMapid) {
            contentValues.put(MAP_NUMBER_COLUMN, RecordingMedium.getWorkMapId());
        }

        return mDb.insert(tableName, null, contentValues);
    }

    public long getLastRowId(String tableName) {
        Cursor cursor = mDb.rawQuery("SELECT crowid FROM " + tableName + " ORDER BY crowid desc limit 1", null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return 0;
    }


    public boolean addPointList(List<LocaDate> dataList, String currentJwdName, String fid) {
        long startTime = System.currentTimeMillis();
        mDb.beginTransaction();
        boolean isSucceed = false;
        try {
            int len = dataList.size();
            for (int i = 0; i < len; i++) {
                LocaDate point = dataList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DbHelperDbHelper.JWD_FID_COLUMN, fid);
                contentValues.put(DbHelperDbHelper.JWD_JD_COLUMN, point.lng + "");
                contentValues.put(DbHelperDbHelper.JWD_WD_COLUMN, point.lat + "");
                contentValues.put(DbHelperDbHelper.JWD_CJDX_COLUMN, "T_ld");
                contentValues.put(DbHelperDbHelper.JWD_CJSJ_COLUMN, new Date().getTime() + "");
                mDb.insert(currentJwdName, null, contentValues);
            }
            mDb.setTransactionSuccessful();
            isSucceed = true;
        } catch (Exception e) {
            LoggerUtils.d("批量保存点出错", "");
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        LoggerUtils.d("存储耗时:", "保存" + dataList.size() + "个点" + (System.currentTimeMillis() - startTime) + "ms");
        return isSucceed;
    }

    //修改点对象属性
    public long updateTable(String tableName, String BMMC, String BM, Map<String, String> param) {
        if (BMMC == null || BM == null) {
            return -1;
        }

        ContentValues contentValues = new ContentValues();
        Set<String> strings = param.keySet();
        for (String s : strings) {
            String s1s = param.get(s);
            contentValues.put(s, s1s);
        }
        return mDb.update(tableName, contentValues, BMMC + "= ?", new String[]{BM});
    }

    public boolean removeMapByid(int id) {
        return mDb.delete(MAP_TABLE, MAP_NUMBER_COLUMN + " = " + id, null) > 0;
    }

    public boolean removeAllCall() {
        return mDb.delete(MAP_TABLE, null, null) > 0;
    }

    //查询所有工作地图
    public List<Maps> getAllMap() {
        List<Maps> list = new ArrayList<>();
        String sql = "select * from " + MAP_TABLE;
        Cursor cursor = mDb.rawQuery(sql, null);
        int count = cursor.getCount();
        while (cursor.moveToNext()) {
            Maps m = new Maps();
            m.mId = cursor.getInt(cursor.getColumnIndex(MAP_NUMBER_COLUMN));
            m.mName = cursor.getString(cursor.getColumnIndex(MAP_MAPNAME_COLUMN));
            m.cjsj = cursor.getString(cursor.getColumnIndex(MAP_CJSJ_COLUMN));
            m.dqsy = cursor.getString(cursor.getColumnIndex(MAP_SFDQSY_COLUMN));
            list.add(m);
            LoggerUtils.d("getAllMap length", m.toString());
        }

        cursor.close();
        return list;
    }

    public List getAllLX() {
        ArrayList list = new ArrayList();
        String sql = "select lxbm,lxmc from " + LX_TABLE + " order by lxbm";
        Cursor cursor = mDb.rawQuery(sql, null);
        int count = cursor.getCount();
        while (cursor.moveToNext()) {
            ArrayList rlist = new ArrayList();
            rlist.add(0, cursor.getString(cursor.getColumnIndex(LX_LXBM)));
            rlist.add(1, cursor.getString(cursor.getColumnIndex(LX_LXMC)));
            list.add(rlist);
        }
        cursor.close();
        return list;
    }

    public List<LdData> getSearchLdList(String ldbm, int mapid) {
        List<LdData> list = new ArrayList();
        String sql = "select LDBM,LDMC,LDXLH,QDJWDID,ZDJWDID,SSLX,MAPID from T_ld " +
                "where LDBM like '%" + ldbm + "%' and MAPID = " + mapid + " order by ldbm";
        Cursor cursor = mDb.rawQuery(sql, null);
        int count = cursor.getCount();
        while (cursor.moveToNext()) {
            LdData ldData = new LdData();
            ldData.setLDBM(cursor.getString(0));
            ldData.setLDMC(cursor.getString(1));
            ldData.setLDXLH(cursor.getInt(2));
            ldData.setQDJWDID(cursor.getInt(3));
            ldData.setZDJWDID(cursor.getInt(4));
            ldData.setSSLX(cursor.getString(5));
            ldData.setMAPID(cursor.getInt(6));
            list.add(ldData);
        }
        cursor.close();
        return list;
    }

    public ArrayList<LocaDate> querySearchLD(String lxbm, int qdjwdid, int zdjwdid, int mapid) {
        ArrayList<LocaDate> res = null;
        String tname = JwdPrefix + mapid;
        String sql = "select JD,WD from " + tname + " where FID='" + lxbm + "' AND crowid >= " + qdjwdid + " AND crowid <= " + zdjwdid + " AND CJDX = 'T_ld' ;";
        Cursor cursor = mDb.rawQuery(sql, null);
        LoggerUtils.d(TAG, sql);
        int count = cursor.getCount();
        if (count <= 0) {
        } else {
            res = new ArrayList<>();
        }
        while (cursor.moveToNext()) {
            Double lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex("JD")));
            Double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("WD")));
            LoggerUtils.d(TAG, "查询获取经/纬度: " + lng + " / " + lat);
            res.add(new LocaDate.Builder().initSite(new Point(lat, lng)).builder());
        }
        cursor.close();
        return res;
    }

    //查询所有属性，根据表名
    public Cursor queryAttrTablebyTName(String tname) {
        String sql = "select * from " + tname;
        return mDb.rawQuery(sql, null);
    }

    //查询经纬度， 根据编码，所属经纬度表
    public LocaDate queryPointByBMAndJwd(String bm, String mapid) {
        String tname = JwdPrefix + mapid;
        String sql = "select JD,WD from " + tname + " where FID='" + bm + "';";
        Cursor cursor = mDb.rawQuery(sql, null);
        LoggerUtils.d(TAG, sql);
        int count = cursor.getCount();
        if (cursor.moveToNext()) {
            Double lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex("JD")));
            Double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("WD")));
            cursor.close();
            LoggerUtils.d(TAG, "查询获取经/纬度: " + lng + " / " + lat);
            return new LocaDate.Builder().initSite(new Point(lat, lng)).builder();
        }
        return null;
    }

    public ArrayList<LocaDate> queryLDByBMAndJwd(String lxbm, String ldbm, String mapid) {
        ArrayList<LocaDate> res = null;
        String tname = JwdPrefix + mapid;
        String ldsql = "select * from T_ld WHERE LDBM='" + ldbm + "';";
        LoggerUtils.d(TAG, ldsql);
        Cursor ldCursor = mDb.rawQuery(ldsql, null);
        if (ldCursor.moveToNext()) {
            long qdjwdid = ldCursor.getLong(ldCursor.getColumnIndex("QDJWDID"));
            long zdjwdid = ldCursor.getLong(ldCursor.getColumnIndex("ZDJWDID"));
            ldCursor.close();
            LoggerUtils.d(TAG, "起点终点经纬度id" + qdjwdid + " / " + zdjwdid);
            String sql = "select JD,WD from " + tname + " where FID='" + lxbm + "' AND crowid >= " + qdjwdid + " AND crowid <= " + zdjwdid + " AND CJDX = 'T_ld' ;";
            Cursor cursor = mDb.rawQuery(sql, null);
            LoggerUtils.d(TAG, sql);
            int count = cursor.getCount();
            if (count <= 0) {
            } else {
                res = new ArrayList<>();
            }
            while (cursor.moveToNext()) {
                Double lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex("JD")));
                Double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("WD")));
                LoggerUtils.d(TAG, "查询获取经/纬度: " + lng + " / " + lat);
                res.add(new LocaDate.Builder().initSite(new Point(lat, lng)).builder());
            }
            cursor.close();
        }


        return res;
    }

    private abstract class DbHelper extends SQLiteOpenHelper {


        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            execSqlTable(db);
        }

        protected abstract void execSqlTable(SQLiteDatabase db);

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading from version " +
                    oldVersion + " to " +
                    newVersion + ", which will destroy all old data");

//            db.execSQL("DROP TABLE IF EXISTS " + MAP_TABLE + ";");
//            db.execSQL("DROP TABLE IF EXISTS " + JWD_TABLE + ";");
//            onCreate(db);
        }
    }

    private List<String> getsAttributeTableSQL() {
        List<String> list = new ArrayList<>();

        List<TableContext> tableContexts = ConfigManager.create().getTablecontextList();

        if (tableContexts.size() < 1) {
            LoggerUtils.d("tableContexts长度：", "构建失败");
        }

        for (TableContext t : tableContexts) {
            String tName = t.getTName(); //表名
            String sql = "CREATE TABLE " + tName + "(";
            List<Fields> fields = t.getFields();
            for (int i = 0; i < fields.size(); i++) {
                Fields f = fields.get(i);
                String fName = f.getFName();

                if (i == 0) {
                    sql = sql + fName + " " + "varchar(25) NOT NULL ,";
                } else {
                    sql = sql + fName + " " + "TEXT NOT NULL,";
                    if (i == fields.size() - 1) {
                        sql = " " + sql + " MAPID INTEGER NOT NULL,";
                        sql = sql + " foreign key (MAPID) references " + MAP_TABLE + " ("
                                + MAP_NUMBER_COLUMN + "),";
                    }
                }
            }

            int length = sql.length();
            sql = sql.substring(0, length - 1);
            sql = sql + ");";
            LoggerUtils.d("tableContexts：", sql + "");
            list.add(sql);
        }
        return list;
    }

    public Cursor excuteExporSql(String table, String mapid) {
        table = "select * from " + table + " where MAPID=" + mapid;
        LoggerUtils.d(TAG, table);
        return mDb.rawQuery(table, null);
    }

    public Cursor excuteExporSql(String table) {
        table = "select * from " + table;
        LoggerUtils.d(TAG, table);
        return mDb.rawQuery(table, null);
    }

    public static String JwdPrefix = "T_JWD_";

    /**
     * 根据工作地图名字新增经纬度表
     */
    public void newJWDTable(int mapid) {
        String JWD_TABLE = JwdPrefix + mapid;

        String sql_T_JWD = "CREATE TABLE " + JWD_TABLE + " (" +
                JWD_CROWID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                JWD_FID_COLUMN + " varchar(50) NOT NULL," +
                JWD_CJDX_COLUMN + " varchar(50) NOT NULL," +
                JWD_JD_COLUMN + " numeric(11,8)," +
                JWD_WD_COLUMN + " numeric(11,8)," +
                JWD_CJSJ_COLUMN + " Date" +
//            " foreign key (" + JWD_FID_COLUMN + ") references " + MAP_TABLE + " (" + MAP_NUMBER_COLUMN + ")" +
                ")";
        LoggerUtils.d("sql_T_JWD", sql_T_JWD);
        PreferencesUtils.putInt(SynopsisObj.getAppContext(), Constants.TABLE_JWD_MAX_, mapid);
        mDb.execSQL(sql_T_JWD);
    }


    /**
     * @param tname 表名
     *              删除表
     */
    public void deleteTableByTableName(String tname) {
        String sql = "DROP TABLE " + tname + ";";
        mDb.execSQL(sql);
    }

    /**
     * tname 表名
     * 删除记录根据mapid
     */

    //    public boolean removeEvent(long rowIndex) {
//        return mDb.delete(EVENT_TABLE, ROW_ID + " = " + rowIndex, null) > 0;
//    }
    public void deleteaRecordByTableName(int mapid, List<TableContext> tableNames) {
        for (TableContext table : tableNames) {
            System.out.println(table.getTName());
            mDb.delete(table.getTName(), MAP_NUMBER_COLUMN + " = " + mapid + "", null);
        }
    }
}
