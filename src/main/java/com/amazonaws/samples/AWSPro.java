package com.amazonaws.samples;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.InvalidParameterException;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.rekognition.model.SearchFacesRequest;
import com.amazonaws.services.rekognition.model.SearchFacesResult;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class AWSPro{	
	
	public static void main(String args[]) throws IOException{
		
		
		Webcam webcam = Webcam.getDefault();
		System.out.println(webcam.getName());			
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open(true);
		OpenCV cv = new OpenCV();
		cv.setVisible(true);
		System.out.println(webcam.getImage().getWidth());
		String photo = "pic3.jpg";
		BufferedImage originalImage;
		BufferedImage targetImage;
		originalImage = ImageIO.read(new File("D:\\java-neon\\eclipse\\java\\Project\\resources\\pic4.jpg"));
		targetImage = ImageIO.read(new File("D:\\java-neon\\eclipse\\java\\Project\\resources\\beatles1.jpg"));
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
			System.out.println(credentials.getAWSAccessKeyId());
			System.out.println(credentials.getAWSSecretKey());

		}catch(Exception e){
			throw new AmazonClientException("cannot");
		}
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_WEST_2)
						.withCredentials(new AWSStaticCredentialsProvider(credentials))
						.build();
				BufferedImage bufferedImage;
				while(true){
					
					bufferedImage = webcam.getImage();
					ByteArrayOutputStream bs = new ByteArrayOutputStream();
					
					try {
						ImageIO.write(bufferedImage, "jpg", bs);
						bs.flush();
						byte[] imageIn = bs.toByteArray();
						
						ByteBuffer imageBytes3 = ByteBuffer.wrap(imageIn);		
						System.out.println("사진 변환 완료");

						//compareFace(imageBytes1, imageBytes3, rekognitionClient);
						searchFacesMatch(rekognitionClient, imageBytes3, "MyCollection");
						System.out.println("============== 비교 후");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						System.out.println("일치하는 얼굴이 없습니다");
						
					}
//					
//					try {						
//						Thread.sleep(50);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
				}
			}
			
		}).start();
		
/*		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_WEST_2)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
		
		//displayFaceDetail(imageBytes1, rekognitionClient, photo);
		//displayLabel(imageBytes1, rekognitionClient, photo);
		compareFace(imageBytes1, imageBytes2, rekognitionClient);*/
		
		
		
		
	}
	
	private static void searchFacesMatch(AmazonRekognition rekognitionClient, ByteBuffer imageBytes1, String collectionId) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		
		Image image = new Image().withBytes(imageBytes1);
		
		SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
				.withCollectionId(collectionId)
				.withImage(image)
				.withFaceMatchThreshold(70F)
				.withMaxFaces(2);
		List<FaceMatch> faceImageMatches = null;
		
		SearchFacesByImageResult searchFacesByImageResult = rekognitionClient.searchFacesByImage(searchFacesByImageRequest);
		faceImageMatches = searchFacesByImageResult.getFaceMatches();
		
		
		
		for(FaceMatch face: faceImageMatches){
			try {
				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
				System.out.println(face.getSimilarity());
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	private static void createCollection(AmazonRekognition rekognitionClient, ByteBuffer imageByte){
		String collectionId = "MyCollection";
		/*System.out.println("Creating collection: " + collectionId);
		
		CreateCollectionRequest request = new CreateCollectionRequest().withCollectionId(collectionId);
		
		CreateCollectionResult createCollectionResult = rekognitionClient.createCollection(request);
		System.out.println("CollectionArn : " + createCollectionResult.getCollectionArn());
		System.out.println("Status code : " + createCollectionResult.getStatusCode().toString());*/
		
		Image image = new Image().withBytes(imageByte);
		
		IndexFacesRequest indexFacesRequest = new IndexFacesRequest().withImage(image).withCollectionId(collectionId)
				.withExternalImageId("sample").withDetectionAttributes("ALL");
		
		IndexFacesResult indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);
		
		List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
		for(FaceRecord faceRecord : faceRecords){
			System.out.println("face detected : Faceid is " + faceRecord.getFace().getFaceId());
			System.out.println("face detected : Faceid is " + faceRecord.getFaceDetail().toString());
		}	
	}
	
	private static void searchFace(AmazonRekognition rekognitionClient) throws IOException{
		String collectionID = "MyCollection";
		String faceId = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		SearchFacesRequest searchFacesRequest = new SearchFacesRequest().withCollectionId(collectionID)
				.withFaceId(faceId).withFaceMatchThreshold(70F).withMaxFaces(2);
		
		SearchFacesResult searchFacesByIdResult = rekognitionClient.searchFaces(searchFacesRequest);
		
		System.out.println("Face matching faceId " + faceId);
		List<FaceMatch> faceImageMatches = searchFacesByIdResult.getFaceMatches();
		for(FaceMatch face: faceImageMatches){
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
		}
		
	}
	
	private static void compareFace(ByteBuffer imageBytes1, ByteBuffer imageBytes2, AmazonRekognition rekognitionClient){
		System.out.println("비교 시작");
		CompareFacesRequest cfRequest = new CompareFacesRequest().withTargetImage(new Image().withBytes(imageBytes2))
				.withSourceImage(new Image().withBytes(imageBytes1));
		System.out.println("클라이언트 시작");
		
		CompareFacesResult cfResult = rekognitionClient.compareFaces(cfRequest);
		
		System.out.println("=====클라이언트 완료");
		List<CompareFacesMatch> result = cfResult.getFaceMatches();
		List<ComparedFace> unmatchedResult = cfResult.getUnmatchedFaces();
		
		for(CompareFacesMatch cfm : result){
			System.out.println("Same person : " + cfm.toString());
		}
		
		for(ComparedFace cf : unmatchedResult){
			System.out.println("Not same person : " + cf.toString());
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
