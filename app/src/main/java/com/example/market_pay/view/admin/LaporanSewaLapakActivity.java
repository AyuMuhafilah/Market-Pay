package com.example.market_pay.view.admin;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.market_pay.R;
import com.example.market_pay.model.BayarSewaModel;
import com.example.market_pay.model.BayarSewaWithMerchant;
import com.example.market_pay.utils.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LaporanSewaLapakActivity extends AppCompatActivity {

    private ImageView back;
    private MaterialButton btnDownload;
    private String bulanLaporan = "";
    private String tahunLaporan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_sewa_lapak);

        back = findViewById(R.id.iconBack);
        btnDownload = findViewById(R.id.btnDownload);

        back.setOnClickListener(v -> finish());
        btnDownload.setOnClickListener(v -> generatePdfLaporan());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lap_sewa_lapak), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void generatePdfLaporan() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<BayarSewaWithMerchant> listData = new ArrayList<>();

        db.collection("users")
                .whereEqualTo("role", "merchant")
                .get()
                .addOnSuccessListener(userDocs -> {
                    if (userDocs.isEmpty()) {
                        Toast.getInstance(this).showToast("Tidak ada merchant ditemukan");
                        return;
                    }

                    List<String> merchantIds = new ArrayList<>();
                    Map<String, String> merchantNames = new HashMap<>();

                    for (QueryDocumentSnapshot userDoc : userDocs) {
                        String uid = userDoc.getId();
                        String nama = userDoc.contains("nama_lengkap") ? userDoc.getString("nama_lengkap") : "Tidak Diketahui";
                        merchantIds.add(uid);
                        merchantNames.put(uid, nama);
                    }

                    db.collection("bayar_sewa")
                            .orderBy("tgl_bayar", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(sewaDocs -> {
                                Map<String, BayarSewaModel> bayarMap = new HashMap<>();

                                for (QueryDocumentSnapshot sewaDoc : sewaDocs) {
                                    BayarSewaModel bayar = sewaDoc.toObject(BayarSewaModel.class);
                                    bayarMap.put(bayar.getUser_id(), bayar);
                                }

                                for (String merchantId : merchantIds) {
                                    if (bayarMap.containsKey(merchantId)) {
                                        BayarSewaModel bayar = bayarMap.get(merchantId);
                                        String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                                .format(bayar.getTgl_bayar().toDate());

                                        if (bulanLaporan.isEmpty()) {
                                            Locale locale = new Locale("id", "ID");
                                            bulanLaporan = new SimpleDateFormat("MMMM", locale).format(bayar.getTgl_bayar().toDate()).toUpperCase();
                                            tahunLaporan = new SimpleDateFormat("yyyy", locale).format(bayar.getTgl_bayar().toDate());
                                        }

                                        String nominal = "Rp " + bayar.getNominal();
                                        listData.add(new BayarSewaWithMerchant(
                                                formattedDate,
                                                merchantNames.get(merchantId),
                                                "-",
                                                nominal,
                                                "LUNAS"
                                        ));
                                    } else {
                                        listData.add(new BayarSewaWithMerchant(
                                                "-",
                                                merchantNames.get(merchantId),
                                                "-",
                                                "-",
                                                "BELUM BAYAR"
                                        ));
                                    }
                                }

                                buildPdfFromData(listData);
                            })
                            .addOnFailureListener(e -> {
                                Toast.getInstance(this).showToast("Gagal ambil data bayar_sewa");
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.getInstance(this).showToast("Gagal ambil data user merchant");
                });
    }

    private void buildPdfFromData(List<BayarSewaWithMerchant> dataList) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(18);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));

        String title = "Laporan Pembayaran Sewa Lapak Bulan " + bulanLaporan + " " + tahunLaporan;
        float titleWidth = titlePaint.measureText(title);
        canvas.drawText(title, (pageInfo.getPageWidth() - titleWidth) / 2, 50, titlePaint);

        int startX = 40;
        int startY = 100;
        int rowHeight = 40;

        int colNoWidth = 50;
        int colOtherWidth = 116;

        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        Paint headerTextPaint = new Paint();
        headerTextPaint.setColor(Color.BLACK);
        headerTextPaint.setTextSize(14);
        headerTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));

        Paint headerBgPaint = new Paint();
        headerBgPaint.setColor(Color.LTGRAY);
        headerBgPaint.setStyle(Paint.Style.FILL);

        // Header
        canvas.drawRect(startX, startY, startX + colNoWidth + colOtherWidth * 4, startY + rowHeight, headerBgPaint);
        canvas.drawRect(startX, startY, startX + colNoWidth + colOtherWidth * 4, startY + rowHeight, borderPaint);
        canvas.drawText("No", centerText("No", startX, colNoWidth, headerTextPaint), startY + 25, headerTextPaint);
        canvas.drawText("Tanggal", centerText("Tanggal", startX + colNoWidth, colOtherWidth, headerTextPaint), startY + 25, headerTextPaint);
        canvas.drawText("Nama Merchant", centerText("Nama Merchant", startX + colNoWidth + colOtherWidth, colOtherWidth, headerTextPaint), startY + 25, headerTextPaint);
        canvas.drawText("Status", centerText("Status", startX + colNoWidth + colOtherWidth * 2, colOtherWidth, headerTextPaint), startY + 25, headerTextPaint);
        canvas.drawText("Nominal", centerText("Nominal", startX + colNoWidth + colOtherWidth * 3, colOtherWidth, headerTextPaint), startY + 25, headerTextPaint);

        Paint cellTextPaint = new Paint();
        cellTextPaint.setColor(Color.BLACK);
        cellTextPaint.setTextSize(14);

        int y = startY + rowHeight;
        int totalNominal = 0;

        for (int i = 0; i < dataList.size(); i++) {
            BayarSewaWithMerchant row = dataList.get(i);
            canvas.drawRect(startX, y, startX + colNoWidth + colOtherWidth * 4, y + rowHeight, borderPaint);

            canvas.drawText(String.valueOf(i + 1), centerText(String.valueOf(i + 1), startX, colNoWidth, cellTextPaint), y + 25, cellTextPaint);
            canvas.drawText(row.tanggal, centerText(row.tanggal, startX + colNoWidth, colOtherWidth, cellTextPaint), y + 25, cellTextPaint);
            canvas.drawText(row.namaUser, centerText(row.namaUser, startX + colNoWidth + colOtherWidth, colOtherWidth, cellTextPaint), y + 25, cellTextPaint);
            canvas.drawText(row.status, centerText(row.status, startX + colNoWidth + colOtherWidth * 2, colOtherWidth, cellTextPaint), y + 25, cellTextPaint);
            canvas.drawText(row.nominal, centerText(row.nominal, startX + colNoWidth + colOtherWidth * 3, colOtherWidth, cellTextPaint), y + 25, cellTextPaint);

            if (row.status.equals("LUNAS")) {
                try {
                    String nominalStr = row.nominal.replace("Rp", "").replace(".", "").replace(" ", "");
                    totalNominal += Integer.parseInt(nominalStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            y += rowHeight;
        }

        canvas.drawRect(startX, y, startX + colNoWidth + colOtherWidth * 4, y + rowHeight, borderPaint);
        canvas.drawText("TOTAL", centerText("TOTAL", startX, colNoWidth + colOtherWidth * 3, cellTextPaint), y + 25, cellTextPaint);
        canvas.drawText("Rp " + totalNominal, centerText("Rp " + totalNominal, startX + colNoWidth + colOtherWidth * 3, colOtherWidth, cellTextPaint), y + 25, cellTextPaint);

        pdfDocument.finishPage(page);
        savePdfToDownloads(pdfDocument);
    }

    private float centerText(String text, int colStartX, int colWidth, Paint paint) {
        float textWidth = paint.measureText(text);
        return colStartX + (colWidth - textWidth) / 2;
    }

    private void savePdfToDownloads(@NonNull PdfDocument document) {
        String fileName = "Laporan_Pembayaran_Sewa_" + System.currentTimeMillis() + ".pdf";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            try {
                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) throw new IOException("Gagal membuat URI");

                try (OutputStream out = resolver.openOutputStream(uri)) {
                    if (out == null) throw new IOException("Gagal membuka OutputStream");
                    document.writeTo(out);
                    Toast.getInstance(this).showToast("PDF berhasil disimpan di folder Download");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.getInstance(this).showToast("Gagal menyimpan PDF: " + e.getMessage());
            } finally {
                document.close();
            }
        } else {
            Toast.getInstance(this).showToast("Penyimpanan PDF hanya didukung di Android 10 ke atas");
        }
    }
}
