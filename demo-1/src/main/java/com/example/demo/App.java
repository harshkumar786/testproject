package com.example.demo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class App {
	private static final String ALGORITHM = "HmacSHA1";
	 private final static String PUBLIC_API_KEY = "Zrso5Pq4YXtBzDIvNm3ZEjKjCZU=";
	    private final static String PRIVATE_API_KEY = "qN5TAompCPTw6guUunXYXSyHX0Y=";
	private final static String IMAGE_KIT_IMAGE_UPLOAD_URL = "https://upload.imagekit.io/rest/api/image/v2/demoTest/";
	private final static String imagePath = "https://pixabay.com/get/57e0d2414250af14f6da8c7dda793e7b173dd6e6514c704c70267ed19545c550_1280.jpg";

	public static void main(String[] args) throws UnirestException {
		//App.main();
	}
	public static JSONObject main() throws UnirestException, IOException {
		if(Files.exists(Paths.get(""))) {
			
		}
		URL url = new URL(imagePath);
		BufferedImage img = ImageIO.read(url);
		File file = new File("downloaded.jpg");
		ImageIO.write(img, "jpg", file);
		file.exists();
		String filename = file.getName().toString();
		String time = timestamp();
		String content = "apiKey=" + PUBLIC_API_KEY + "&filename=" + filename + "&timestamp=" + time;
		String sig = sign(content);

		//org.apache.commons.io.FileUtils.copyURLToFile(url, file);
		try {
			HttpResponse<JsonNode> uploadResponse = Unirest.post(IMAGE_KIT_IMAGE_UPLOAD_URL)
					.header("accept", "application/json").field("file", file).field("filename", filename)
					.field("apiKey", PUBLIC_API_KEY).field("signature", sig).field("timestamp", time).field("folder", "/images/folder")
					// .field("useUniqueFilename", false) // To override existing image. (Default:
					// True)
					.asJson();
			JSONObject jsonResponse = uploadResponse.getBody().getObject();
			System.out.println(jsonResponse.toString());
			return jsonResponse;
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String sign(String content) {
		String encoded;
		try {
			SecretKeySpec signingKey = new SecretKeySpec(PRIVATE_API_KEY.getBytes(), ALGORITHM);
			Mac mac = Mac.getInstance(ALGORITHM);
			mac.init(signingKey);
			encoded = toHexString(mac.doFinal(content.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Cannot create signature.", e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Cannot create signature.", e);
		}
		return encoded;
	}

	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

	protected static String timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000L);
	}

}