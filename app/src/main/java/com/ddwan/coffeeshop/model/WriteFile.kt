package com.ddwan.coffeeshop.model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.collections.ArrayList

class WriteFile(var context: Context) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun writeFile(list: ArrayList<Bill>, listMoney: ArrayList<Int>): Boolean {
        val filePaths: String =
            context.getExternalFilesDir(null).toString() + "/bao_cao_doanh_thu.csv"
        try {
            val file = File(filePaths)
            val writer = Files.newBufferedWriter(Paths.get(file.toURI()))

            val csvPrinter =
                CSVPrinter(
                    writer,
                    CSVFormat.DEFAULT.withHeader(
                        "Bill ID",
                        "Table ID",
                        "Date Check In",
                        "Date Check Out",
                        "User",
                        "Price"
                    )
                )
            var tong = 0
            for ((i, item) in list.withIndex()) {
                csvPrinter.printRecord(
                    item.billId,
                    item.tableId,
                    item.dateCheckIn,
                    item.dateCheckOut,
                    item.userId,
                    listMoney[i]
                )
                tong += listMoney[i]
            }
            csvPrinter.printRecord(
                "Sum",
                "",
                "",
                "",
                "",
                tong
            )
            csvPrinter.flush()
            csvPrinter.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}