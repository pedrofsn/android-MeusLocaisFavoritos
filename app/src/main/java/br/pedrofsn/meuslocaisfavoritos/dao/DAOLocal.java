package br.pedrofsn.meuslocaisfavoritos.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.pedrofsn.meuslocaisfavoritos.interfaces.IBancoDeDados;
import br.pedrofsn.meuslocaisfavoritos.model.Local;

/**
 * Created by pedrofsn on 04/12/2014.
 */
public class DAOLocal extends SQLiteOpenHelper implements IBancoDeDados {

    private static final String DATABASE_NAME = "MeusLocaisFavoritos.db";
    private static final int VERSION = 2;

    private static final String TABELA_LOCAIS_FAVORITOS = "LOCAIS_FAVORITOS";
    private static final String COLUNA_ID = "ID";
    private static final String COLUNA_ENDERECO = "ENDERECO";
    private static final String COLUNA_CIDADE = "CIDADE";
    private static final String COLUNA_PAIS = "PAIS";
    private static final String COLUNA_NOME = "NOME";
    private static final String COLUNA_LATITUDE = "LATITUDE";
    private static final String COLUNA_LONGITUDE = "LONGITUDE";
    private static final String COLUNA_DATA_CHECKIN = "DATA_CHECKIN";

    public DAOLocal(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTable = "CREATE TABLE " + TABELA_LOCAIS_FAVORITOS +
                "(" + COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUNA_ENDERECO + " STRING, " +
                COLUNA_CIDADE + " STRING, " +
                COLUNA_PAIS + " STRING, " +
                COLUNA_NOME + " STRING, " +
                COLUNA_LATITUDE + " NUMBER NOT NULL, " +
                COLUNA_LONGITUDE + " NUMBER NOT NULL, " +
                COLUNA_DATA_CHECKIN + " NUMBER NOT NULL " +
                ");";
        database.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void createLocal(Local local) {
        try {
            Date date = new Date();
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUNA_ENDERECO, local.getEndereco());
            values.put(COLUNA_CIDADE, local.getCidade());
            values.put(COLUNA_PAIS, local.getPais());
            values.put(COLUNA_NOME, local.getNome());
            values.put(COLUNA_LATITUDE, local.getLatitude());
            values.put(COLUNA_LONGITUDE, local.getLongitude());
            values.put(COLUNA_DATA_CHECKIN, date.getTime());
            db.insert(TABELA_LOCAIS_FAVORITOS, null, values);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Local> readLocal() {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            List<Local> listaLocais = new ArrayList<Local>();
            database = getReadableDatabase();
            cursor = database.query(TABELA_LOCAIS_FAVORITOS, new String[]{
                    COLUNA_ID,
                    COLUNA_ENDERECO,
                    COLUNA_CIDADE,
                    COLUNA_PAIS,
                    COLUNA_NOME,
                    COLUNA_LATITUDE,
                    COLUNA_DATA_CHECKIN,
                    COLUNA_LONGITUDE}, null, null, null, null, COLUNA_ID);

            if (cursor.moveToFirst()) do {
                Local local = new Local();
                local.setId(cursor.getLong(cursor.getColumnIndex(COLUNA_ID)));
                local.setEndereco(cursor.getString(cursor.getColumnIndex(COLUNA_ENDERECO)));
                local.setCidade(cursor.getString(cursor.getColumnIndex(COLUNA_CIDADE)));
                local.setPais(cursor.getString(cursor.getColumnIndex(COLUNA_PAIS)));
                local.setNome(cursor.getString(cursor.getColumnIndex(COLUNA_NOME)));
                local.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUNA_LATITUDE)));
                local.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUNA_LONGITUDE)));
                local.setDataDoCheckin(cursor.getLong(cursor.getColumnIndex(COLUNA_DATA_CHECKIN)));
                listaLocais.add(local);
            } while (cursor.moveToNext());
            cursor.close();
            database.close();
            return listaLocais;
        } catch (Exception e) {
            if (cursor != null) cursor.close();
            if (database != null) database.close();
            throw e;
        }
    }

    @Override
    public void deleteLocal(long id) {
        SQLiteDatabase database = null;
        try {
            database = getWritableDatabase();
            int rowsDeleted = database.delete(TABELA_LOCAIS_FAVORITOS, " ID = ?", new String[]{String.valueOf(id)});
            if (rowsDeleted == 0) {
                throw new RuntimeException("Failed to delete row. The database.delete() method informed 0 rows were affected.");
            }
            database.close();
        } catch (Exception e) {
            if (database != null) database.close();
            throw e;
        }
    }

    @Override
    public boolean existsLocal(LatLng latLng) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("select * from " + TABELA_LOCAIS_FAVORITOS + " where " + COLUNA_LATITUDE + " like '" + String.valueOf(latLng.latitude) + "' and " + COLUNA_LONGITUDE + " like '" + String.valueOf(latLng.longitude) + "'", null);
        return mCursor.moveToFirst();
    }

}
