package com.example.demo.user;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
@Service
public class QrCodeService {
    @Autowired
    private ScanTokenRepository scanTokenRepository;

    public byte[] generateUserQrCode(String email) throws Exception{
        ScanToken scanToken = new ScanToken();
        scanToken.setUserEmail(email);
        scanToken.setExpiresAt(LocalDateTime.now().plusMinutes(5)); //adjust accordingly
        scanToken = scanTokenRepository.save(scanToken);
        String qrData = scanToken.getToken().toString();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 250, 250);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}
