package li.itcc.hackathon15.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import li.itcc.hackathon15.services.PoiListBean;

/**
 * Created by patrik on 12/09/2015.
 */
public class PoiTableUpdater  {


    private final Context fContext;
    private PoiDBOpenHelper dbOpenHelper;

    public PoiTableUpdater(Context context) {
        fContext = context;
    }

    public void updatePoiTable(PoiListBean listBean) throws Exception {

        dbOpenHelper = new PoiDBOpenHelper(fContext);
        SQLiteDatabase dbWrite = dbOpenHelper.getWritableDatabase();

        String sql = "insert into " + PoiDatabaseConstants.TABLE_POIS + "(POI_NAME, POI_LONGITUDE, POI_LATITUDE) VALUES (?,?,?)";
        SQLiteStatement insert = dbWrite.compileStatement(sql);

        for (int i = 0; i < listBean.getAllPolis().length; i++) {

            insert.bindString(1, listBean.getAllPolis()[i].getPoiName());
            insert.bindDouble(2, listBean.getAllPolis()[i].getLongitude());
            insert.bindDouble(3, listBean.getAllPolis()[i].getLatitude());
            insert.execute();

        }


        SQLiteDatabase dbRead = dbOpenHelper.getReadableDatabase();

        String sqlread = "select * from" + PoiDatabaseConstants.TABLE_POIS;
        //Log.d("DB", dbRead.query(""));

    }

}
