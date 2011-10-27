package com.cloudspokes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

import com.cloudspokes.process.ChallengeCategory;
import com.cloudspokes.process.ChallengeParticipantProcess;
import com.cloudspokes.process.ChallengeProcess;
import com.cloudspokes.process.CommentProcess;
import com.cloudspokes.process.DataLoaderProcess;
import com.cloudspokes.process.MemberPhotoProcess;
import com.cloudspokes.process.MemberProcess;
import com.cloudspokes.process.PrizeProcess;
import com.cloudspokes.process.RecommendationProcess;
import com.cloudspokes.process.ReviewerProcess;
import com.cloudspokes.process.SubmissionFile;
import com.cloudspokes.process.SubmissionOther;
import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.CSVReader;
import com.sforce.async.ContentType;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;
import com.sforce.async.RestConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class BulkLoader {

	static Logger logger = Logger.getLogger(BulkLoader.class);
	// Salesforce.com credentials
	private String userName;
	private String password;
	private String endPoint;
	private String apiVersion;
	// SQLServer connection URL
	private String sqlServerUrl;
	private String sqlServerUsername;
	private String sqlServerPassword;
	// the CSV file being uploaded
	private String csvFileDirectory;
	// location where the properties are stored
	String propertiesFile = "bulkloader.properties";
	private BufferedReader console = null;

	public static void main(String[] args) throws AsyncApiException,
			ConnectionException, IOException {
		BasicConfigurator.configure();
		BulkLoader bulk = new BulkLoader();
		bulk.readProperties();
		bulk.runCommandLine();
		//bulk.runAll();
	}
	
	private void readProperties() {
		  	
		//create an instance of properties class
		Properties props = new Properties();
		
		//try retrieve data from file
		try {
			props.load(new FileInputStream(propertiesFile));
			
		} catch(IOException e) {
			try {
				props.load(new FileInputStream("/Users/sts/CloudSpokes Data Loader/" + propertiesFile));
			} catch(IOException e2) {
				e2.printStackTrace();
			}
		}
		
		userName = props.getProperty("userName");
		password = props.getProperty("password");
		endPoint = props.getProperty("endPoint");
		apiVersion = props.getProperty("apiVersion");
		sqlServerUrl = props.getProperty("sqlServerUrl");
		sqlServerPassword = props.getProperty("sqlServerPassword");
		sqlServerUsername = props.getProperty("sqlServerUsername");
		csvFileDirectory = props.getProperty("csvFileDirectory");
	    
	}	

	/**
	 * Allows the user to run multiple operations
	 */
	public void runCommandLine() {

		showMenu();
		console = new BufferedReader(new InputStreamReader(System.in));
		try {
			String choice = console.readLine();
			while ((choice != null) && (Integer.parseInt(choice) != 100)) {

				if (Integer.parseInt(choice) == 1) {
					ChallengeProcess process = new ChallengeProcess(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);

				} else if (Integer.parseInt(choice) == 2) {
						MemberProcess process = new MemberProcess(csvFileDirectory);
						if (createCSVFromSQLServer(process)) runJob(process, userName, password);
					
				} else if (Integer.parseInt(choice) == 4) {
					ChallengeCategory process = new ChallengeCategory(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);
					
				} else if (Integer.parseInt(choice) == 6) {
					RecommendationProcess process = new RecommendationProcess(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);

				} else if (Integer.parseInt(choice) == 7) {
					CommentProcess process = new CommentProcess(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);
					
				} else if (Integer.parseInt(choice) == 9) {
					PrizeProcess process = new PrizeProcess(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);
					
				} else if (Integer.parseInt(choice) == 10) {
					ReviewerProcess process = new ReviewerProcess(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);
					
				} else if (Integer.parseInt(choice) == 11) {
					ChallengeParticipantProcess process = new ChallengeParticipantProcess(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);
					
				} else if (Integer.parseInt(choice) == 12) {
					SubmissionFile process = new SubmissionFile(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);
					
				} else if (Integer.parseInt(choice) == 13) {
					SubmissionOther process = new SubmissionOther(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);
					
				} else if (Integer.parseInt(choice) == 14) {
					MemberPhotoProcess process = new MemberPhotoProcess(csvFileDirectory);
					if (createCSVFromSQLServer(process)) runJob(process, userName, password);
					
				// RUN ALL
				} else if (Integer.parseInt(choice) == 99) {
					runAll();
				}
				showMenu();
				choice = console.readLine();
			}

		} catch (IOException io) {
			handleError(io.getMessage());
		} catch (NumberFormatException nf) {
			runCommandLine();
		} catch (LoginFault lf) {
			handleError(lf.getExceptionMessage());
		} catch (Exception e) {
			e.printStackTrace();
			handleError(e.getMessage());
		}

	}

	/**
	 * Displays a menu in the console to choose the operation
	 */
	public void showMenu() {
		System.out.println("\n1. Challenges");
		System.out.println("2. Members");
		System.out.println("4. Challenge Categories");
		System.out.println("6. Member Recommendations");
		System.out.println("7. Challenge Comments");
		System.out.println("9. Prizes");
		System.out.println("10. Reviewers");
		System.out.println("11. NEW Challenge Participants");
		System.out.println("12. Submission File");
		System.out.println("13. Submission Other");
		System.out.println("14. Member Photos");
		System.out.println("99. Run All");
		System.out.println("100. Exit");
		System.out.println(" ");
		System.out.println("---> Operation: ");
	}

	private void runAll() {

		try {
			
			ChallengeProcess process1 = new ChallengeProcess(csvFileDirectory);
			if (createCSVFromSQLServer(process1)) runJob(process1, userName, password);
	
			MemberProcess process2 = new MemberProcess(csvFileDirectory);
			if (createCSVFromSQLServer(process2)) runJob(process2, userName, password);
			
		
			ChallengeCategory process3 = new ChallengeCategory(csvFileDirectory);
			if (createCSVFromSQLServer(process3)) runJob(process3, userName, password);
			
	
			RecommendationProcess process4 = new RecommendationProcess(csvFileDirectory);
			if (createCSVFromSQLServer(process4)) runJob(process4, userName, password);
	
	
			CommentProcess process5 = new CommentProcess(csvFileDirectory);
			if (createCSVFromSQLServer(process5)) runJob(process5, userName, password);
			
	
			PrizeProcess process6 = new PrizeProcess(csvFileDirectory);
			if (createCSVFromSQLServer(process6)) runJob(process6, userName, password);
			
	
			ReviewerProcess process7 = new ReviewerProcess(csvFileDirectory);
			if (createCSVFromSQLServer(process7)) runJob(process7, userName, password);
			
	
			ChallengeParticipantProcess process8 = new ChallengeParticipantProcess(csvFileDirectory);
			if (createCSVFromSQLServer(process8)) runJob(process8, userName, password);
			
	
			SubmissionFile process9 = new SubmissionFile(csvFileDirectory);
			if (createCSVFromSQLServer(process9)) runJob(process9, userName, password);
			
	
			SubmissionOther process10 = new SubmissionOther(csvFileDirectory);
			if (createCSVFromSQLServer(process10)) runJob(process10, userName, password);
			
	
			MemberPhotoProcess process11 = new MemberPhotoProcess(csvFileDirectory);
			if (createCSVFromSQLServer(process11)) runJob(process11, userName, password);


		} catch (IOException io) {
			io.printStackTrace();
			handleError(io.getMessage());
		} catch (LoginFault lf) {
			handleError(lf.getExceptionMessage());
		} catch (Exception e) {
			e.printStackTrace();
			handleError(e.getMessage());
		}

	}

	private boolean createCSVFromSQLServer(DataLoaderProcess process)
			throws SQLException {

		System.out.println("Fetching records from SQLServer...");

		Connection conn = null;
		ResultSet rs = null;
		boolean success = false;

		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(sqlServerUrl, sqlServerUsername,
					sqlServerPassword);

			Statement s = conn.createStatement();
			s.executeQuery(process.getSql());
			rs = s.getResultSet();

			// dump the contents to the console
			/**
			 * while (rs.next ()){ int idVal = rs.getInt ("id"); String nameVal
			 * = rs.getString ("name"); System.out.println
			 * ("id = "+idVal+", name = "+nameVal); }
			 **/

			// write the result set to the CSV file
			if (rs != null) {
				CSVWriter writer = new CSVWriter(new FileWriter(
						process.getCsvFile()), ',');
				writer.writeAll(rs, true);
				writer.close();
				// System.out.println("Successfully fetched records from SQLServer");
				success = true;
			}

		} catch (Exception e) {
			handleError(e.toString());
			success = false;
		} finally {
			if (rs != null) {
				try {
					rs.close();
					System.out.println("Resultset terminated");
				} catch (Exception e1) { /* ignore close errors */
				}
			}
			if (conn != null) {
				try {
					conn.close();
					System.out.println("Database connection terminated");
				} catch (Exception e2) { /* ignore close errors */
				}
			}

		}
		return success;

	}

	private void handleError(String msg) {
		logger.error(msg);
	}

	/**
	 * Creates a Bulk API job and uploads batches for a CSV file.
	 */
	public void runJob(DataLoaderProcess process, String userName,
			String password) throws AsyncApiException, ConnectionException,
			IOException {
		System.out.println("Pushing job to SFDC..");
		RestConnection connection = getRestConnection(userName, password);
		System.out.println("Connection established..");
		JobInfo job = createJob(process, connection);
		List<BatchInfo> batchInfoList = createBatchesFromCSVFile(connection,
				job, process.getCsvFile());
		closeJob(connection, job.getId());
		awaitCompletion(connection, job, batchInfoList);
		checkResults(connection, job, batchInfoList);
	}

	/**
	 * Gets the results of the operation and checks for errors.
	 */
	private void checkResults(RestConnection connection, JobInfo job,
			List<BatchInfo> batchInfoList) throws AsyncApiException,
			IOException {
		// batchInfoList was populated when batches were created and submitted
		for (BatchInfo b : batchInfoList) {
			CSVReader rdr = new CSVReader(connection.getBatchResultStream(
					job.getId(), b.getId()));
			List<String> resultHeader = rdr.nextRecord();
			int resultCols = resultHeader.size();

			List<String> row;
			while ((row = rdr.nextRecord()) != null) {
				Map<String, String> resultInfo = new HashMap<String, String>();
				for (int i = 0; i < resultCols; i++) {
					resultInfo.put(resultHeader.get(i), row.get(i));
				}
				boolean success = Boolean.valueOf(resultInfo.get("Success"));
				boolean created = Boolean.valueOf(resultInfo.get("Created"));
				String id = resultInfo.get("Id");
				String error = resultInfo.get("Error");
				if (success && created) {
					System.out.println("Created row with id " + id);
				} else if (!success) {
					System.out.println("Failed with error: " + error);
				}
			}
		}
	}

	private void closeJob(RestConnection connection, String jobId)
			throws AsyncApiException {
		JobInfo job = new JobInfo();
		job.setId(jobId);
		job.setState(JobStateEnum.Closed);
		connection.updateJob(job);
	}

	/**
	 * Wait for a job to complete by polling the Bulk API.
	 */
	private void awaitCompletion(RestConnection connection, JobInfo job,
			List<BatchInfo> batchInfoList) throws AsyncApiException {
		long sleepTime = 0L;
		Set<String> incomplete = new HashSet<String>();
		for (BatchInfo bi : batchInfoList) {
			incomplete.add(bi.getId());
		}
		while (!incomplete.isEmpty()) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
			System.out.println("Awaiting results..." + incomplete.size());
			sleepTime = 10000L;
			BatchInfo[] statusList = connection.getBatchInfoList(job.getId())
					.getBatchInfo();
			for (BatchInfo b : statusList) {
				if (b.getState() == BatchStateEnum.Completed
						|| b.getState() == BatchStateEnum.Failed) {
					if (incomplete.remove(b.getId())) {
						System.out.println("BATCH STATUS:\n" + b);
					}
				}
			}
		}
	}

	/**
	 * Create a new job using the Bulk API.
	 */
	private JobInfo createJob(DataLoaderProcess process,
			RestConnection connection) throws AsyncApiException {
		JobInfo job = new JobInfo();
		job.setObject(process.getSobject());
		job.setOperation(process.getOperation());
		if (process.getExternalIdFieldName() != null)
			job.setExternalIdFieldName(process.getExternalIdFieldName());
		job.setContentType(ContentType.CSV);
		job = connection.createJob(job);
		System.out.println(job);
		return job;
	}

	/**
	 * Create the RestConnection used to call Bulk API operations.
	 */
	private RestConnection getRestConnection(String userName, String password)
			throws ConnectionException, AsyncApiException {
		ConnectorConfig partnerConfig = new ConnectorConfig();
		partnerConfig.setUsername(userName);
		partnerConfig.setPassword(password);
		partnerConfig.setAuthEndpoint(endPoint);
		// Creating the connection automatically handles login and stores
		// the session in partnerConfig
		new PartnerConnection(partnerConfig);
		// When PartnerConnection is instantiated, a login is implicitly
		// executed and, if successful,
		// a valid session is stored in the ConnectorConfig instance.
		// Use this key to initialize a RestConnection:
		ConnectorConfig config = new ConnectorConfig();
		config.setSessionId(partnerConfig.getSessionId());
		// The endpoint for the Bulk API service is the same as for the normal
		// SOAP uri until the /Soap/ part. From here it's '/async/versionNumber'
		String soapEndpoint = partnerConfig.getServiceEndpoint();
		String restEndpoint = soapEndpoint.substring(0,
				soapEndpoint.indexOf("Soap/"))
				+ "async/" + apiVersion;
		config.setRestEndpoint(restEndpoint);
		// This should only be false when doing debugging.
		config.setCompression(true);
		// Set this to true to see HTTP requests and responses on stdout
		config.setTraceMessage(false);
		RestConnection connection = new RestConnection(config);
		return connection;
	}

	/**
	 * Create and upload batches using a CSV file. The file into the appropriate
	 * size batch files.
	 */
	private List<BatchInfo> createBatchesFromCSVFile(RestConnection connection,
			JobInfo jobInfo, String csvFileName) throws IOException,
			AsyncApiException {
		List<BatchInfo> batchInfos = new ArrayList<BatchInfo>();
		BufferedReader rdr = new BufferedReader(new InputStreamReader(
				new FileInputStream(csvFileName)));
		// read the CSV header row
		byte[] headerBytes = (rdr.readLine() + "\n").getBytes("UTF-8");
		int headerBytesLength = headerBytes.length;
		File tmpFile = File.createTempFile("bulkAPIInsert", ".csv");

		// Split the CSV file into multiple batches
		try {
			FileOutputStream tmpOut = new FileOutputStream(tmpFile);
			int maxBytesPerBatch = 10000000; // 10 million bytes per batch
			int maxRowsPerBatch = 10000; // 10 thousand rows per batch
			int currentBytes = 0;
			int currentLines = 0;
			String nextLine;
			while ((nextLine = rdr.readLine()) != null) {
				byte[] bytes = (nextLine + "\n").getBytes("UTF-8");
				// Create a new batch when our batch size limit is reached
				if (currentBytes + bytes.length > maxBytesPerBatch
						|| currentLines > maxRowsPerBatch) {
					createBatch(tmpOut, tmpFile, batchInfos, connection,
							jobInfo);
					currentBytes = 0;
					currentLines = 0;
				}
				if (currentBytes == 0) {
					tmpOut = new FileOutputStream(tmpFile);
					tmpOut.write(headerBytes);
					currentBytes = headerBytesLength;
					currentLines = 1;
				}
				tmpOut.write(bytes);
				currentBytes += bytes.length;
				currentLines++;
			}
			// Finished processing all rows
			// Create a final batch for any remaining data
			if (currentLines > 1) {
				createBatch(tmpOut, tmpFile, batchInfos, connection, jobInfo);
			}
		} finally {
			tmpFile.delete();
		}
		return batchInfos;
	}

	/**
	 * Create a batch by uploading the contents of the file. This closes the
	 * output stream.
	 */
	private void createBatch(FileOutputStream tmpOut, File tmpFile,
			List<BatchInfo> batchInfos, RestConnection connection,
			JobInfo jobInfo) throws IOException, AsyncApiException {
		tmpOut.flush();
		tmpOut.close();
		FileInputStream tmpInputStream = new FileInputStream(tmpFile);
		try {
			BatchInfo batchInfo = connection.createBatchFromStream(jobInfo,
					tmpInputStream);
			System.out.println(batchInfo);
			batchInfos.add(batchInfo);

		} finally {
			tmpInputStream.close();
		}
	}
}
