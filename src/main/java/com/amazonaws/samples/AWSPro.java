package com.amazonaws.samples;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.util.IOUtils;

public class AWSPro {
	public static void main(String args[]) throws IOException{
		
		String photo = "pic3.jpg";
		BufferedImage originalImage;
		BufferedImage targetImage;
		originalImage = ImageIO.read(new File("D:\\java-neon\\eclipse\\java\\Project\\resources\\pic1.jpg"));
		targetImage = ImageIO.read(new File("D:\\java-neon\\eclipse\\java\\Project\\resources\\pic4.jpeg"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(originalImage, "jpg", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.reset(); //baos를 reset하지 않으면 baos에 전의 데이터가 그대로 들어가므로 쌓이게 된다.
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		ImageIO.write(targetImage, "jpg", baos);
		baos.flush();		
		byte[] targetimageInByte = baos.toByteArray();
		System.out.println(imageInByte.length);
		System.out.println(targetimageInByte.length);
		baos.close();
		baos1.close();
		ByteBuffer imageBytes1 = ByteBuffer.wrap(imageInByte);
		ByteBuffer imageBytes2 = ByteBuffer.wrap(targetimageInByte);
		//ClassLoader classLoader = new AWSPro().getClass().getClassLoader();
		
		AWSCredentials credentials;
		
		try{
			credentials = new ProfileCredentialsProvider().getCredentials();
			System.out.println(credentials.toString());
		}catch(Exception e){
			throw new AmazonClientException("cannot");
		}
		
		//ByteBuffer imageBytes=null;
		/*try{
			InputStream inputStream = classLoader.getResourceAsStream(photo);
			imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		}catch(IOException e){				
			e.printStackTrace();
		}*/
		
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_WEST_2)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
		
		//displayFaceDetail(imageBytes1, rekognitionClient, photo);
		displayLabel(imageBytes1, rekognitionClient, photo);
		//compareFace(imageBytes1, imageBytes2, rekognitionClient);
		
		
	}
	
	private static void compareFace(ByteBuffer imageBytes1, ByteBuffer imageBytes2, AmazonRekognition rekognitionClient){
		CompareFacesRequest cfRequest = new CompareFacesRequest().withTargetImage(new Image().withBytes(imageBytes2))
				.withSourceImage(new Image().withBytes(imageBytes1));
		CompareFacesResult cfResult = rekognitionClient.compareFaces(cfRequest);
		List<CompareFacesMatch> result = cfResult.getFaceMatches();
		List<ComparedFace> unmatchedResult = cfResult.getUnmatchedFaces();
		for(CompareFacesMatch cfm : result){
			System.out.println("Wow : " + cfm.toString());
		}
		
		for(ComparedFace cf : unmatchedResult){
			System.out.println("NONO : " + cf.toString());
		}
	}
	
	private static void displayFaceDetail(ByteBuffer imageBytes, AmazonRekognition rekognitionClient, String photo){
		DetectFacesRequest dfRequest = new DetectFacesRequest().withImage(new Image().withBytes(imageBytes))
				.withAttributes("ALL");
		
		try{
			DetectFacesResult result = rekognitionClient.detectFaces(dfRequest);
			List<FaceDetail> faceDetails = result.getFaceDetails();
			
			System.out.println("Detected Face for " + photo);
			for(FaceDetail facedetail: faceDetails){
				System.out.println("Face Detail: " + facedetail.toString());
			}
		}catch(AmazonRekognitionException e){
			e.printStackTrace();
		}
	}
	
	private static void displayLabel(ByteBuffer imageBytes, AmazonRekognition rekognitionClient, String photo){
		DetectLabelsRequest request = new DetectLabelsRequest()
				.withImage(new Image().withBytes(imageBytes))
				.withMaxLabels(10)
				.withMinConfidence(77F);
		
		try{
			DetectLabelsResult result = rekognitionClient.detectLabels(request);
			List<Label> labels = result.getLabels();
			
			System.out.println("Detected labels for " + photo);
			for(Label label : labels){
				System.out.println(label.getName() + ": " + label.getConfidence().toString());
			}
		}catch(AmazonRekognitionException e){
			e.printStackTrace();
		}
	}

}
