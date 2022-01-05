package com.anker.bluetoothtool.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anker.bluetoothtool.MyApplication
import com.anker.bluetoothtool.database.dao.CmdFunctionModelDao
import com.anker.bluetoothtool.database.dao.ProductModelDao
import com.anker.bluetoothtool.model.CmdFunctionModel
import com.anker.bluetoothtool.model.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Database(entities = [ProductModel::class, CmdFunctionModel::class], version = 1, exportSchema = false)
abstract class AnkerWorkDatabase : RoomDatabase() {

    abstract val productDao: ProductModelDao
    abstract val functionDao: CmdFunctionModelDao

    companion object {
        @Volatile
        private var INSTANCE: AnkerWorkDatabase? = null

        fun getDatabase(): AnkerWorkDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        MyApplication.context,
                        AnkerWorkDatabase::class.java,
                        "ankerwork_database"
                    )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

//"\"isBle\": false,\n" +
//"\"notifyChara\": \"00008888-0000-1000-8000-00805F9B34FB\",\n" +
//"\"productCode\": \"A3305\",\n" +
//"\"serviceUUID\": \"0124F5DA-0000-1000-8000-00805F9B34FB\",\n" +
//"\"sppUUID\": \"00001101-0000-1000-8000-00805f9b34fb\",\n" +
//"\"writeChara\": \"00007777-0000-1000-8000-00805F9B34FB\"\n" +
//"}, {\n" +
//"\"isBle\": false,\n" +
//"\"notifyChara\": \"00007777-0000-1000-8000-00805F9B34FB\",\n" +
//"\"productCode\": \"A3301\",\n" +
//"\"serviceUUID\": \"0112F5DA-0000-1000-8000-00805F9B34FB\",\n" +
//"\"sppUUID\": \"0CF12D31-FAC3-4553-BD80-D6832E7B3301\",\n" +
//"\"writeChara\": \"00008888-0000-1000-8000-00805F9B34FB\"\n" +
//"}, {\n" +
//"\"isBle\": false,\n" +
//"\"notifyChara\": \"00007777-0000-1000-8000-00805F9B34FB\",\n" +
//"\"productCode\": \"A3302\",\n" +
//"\"serviceUUID\": \"0119F5DA-0000-1000-8000-00805F9B34FB\",\n" +
//"\"sppUUID\": \"0CF12D31-FAC3-4553-BD80-D6832E7B3302\",\n" +
//"\"writeChara\": \"00008888-0000-1000-8000-00805F9B34FB\"\n" +
//"}, {\n" +
//"\"isBle\": false,\n" +
//"\"notifyChara\": \"00008888-0000-1000-8000-00805F9B34FB\",\n" +
//"\"productCode\": \"A3510\",\n" +
//"\"serviceUUID\": \"0135F5DA-0000-1000-8000-00805F9B34FB\",\n" +
//"\"sppUUID\": \"0CF12D31-FAC3-4553-BD80-D6832E7B3510\",\n" +
//"\"writeChara\": \"00007777-0000-1000-8000-00805F9B34FB\"\n" +
//"}]"

