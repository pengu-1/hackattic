package com.readingqr.hackattic;

import java.awt.image.BufferedImage ;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import javax.imageio.ImageIO;

import org.json.JSONObject;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class ReadingQr {
    static final String baseUrl = "https://hackattic.com";

    public void readQr() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("%s/challenges/reading_qr/problem?access_token=bfdf12d3d82c81ed", baseUrl)))
            .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        JSONObject obj = new JSONObject(response.body());
        // System.out.println(obj.get("image_url"));
        BufferedImage  image = ImageIO.read(new URL(obj.get("image_url").toString()));
        String result = parseQr(image);
        if (result == "") {
            System.err.println("Unable to parse qr");
        }
        postAnswer(result, client);
    }

    private void postAnswer(String result, HttpClient client) throws IOException, InterruptedException {
        String body = new JSONObject().put("code", result).toString();
        // NameValuePair body = new BasicNameValuePair("code", result);
        // List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        // params.add(body);
        // post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(String.format("%s/challenges/reading_qr/solve?access_token=bfdf12d3d82c81ed", baseUrl)))
        .POST(BodyPublishers.ofString(body))
        .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        System.out.println(response);
    }
    
    private String parseQr(BufferedImage image) {
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitMap = new BinaryBitmap(new HybridBinarizer (source));
        QRCodeReader reader = new QRCodeReader();
        try {
            Result res = reader.decode(bitMap);
            return res.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
            return "";
        }

    }
}