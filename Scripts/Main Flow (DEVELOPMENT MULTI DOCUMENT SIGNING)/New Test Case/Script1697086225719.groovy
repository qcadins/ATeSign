import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection as Connection
import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.DecryptResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.protobuf.ByteString;
import java.io.IOException;


'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

aaa = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'('099addc695b548aa855c7758baa9474b')

aa = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'('099addc695b548aa855c7758baa9474b', aaa)

public class DecryptSymmetric {

  public void decryptSymmetric() throws IOException {
	// TODO(developer): Replace these variables before running the sample.
	String projectId = "your-project-id";
	String locationId = "us-east1";
	String keyRingId = "my-key-ring";
	String keyId = "my-key";
	byte[] ciphertext = null;
	decryptSymmetric(projectId, locationId, keyRingId, keyId, ciphertext);
  }

  // Decrypt data that was encrypted using a symmetric key.
  public void decryptSymmetric(
	  String projectId, String locationId, String keyRingId, String keyId, byte[] ciphertext)
	  throws IOException {
	// Initialize client that will be used to send requests. This client only
	// needs to be created once, and can be reused for multiple requests. After
	// completing all of your requests, call the "close" method on the client to
	// safely clean up any remaining background resources.
	try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
	  // Build the key version name from the project, location, key ring, and
	  // key.
	  CryptoKeyName keyName = CryptoKeyName.of(projectId, locationId, keyRingId, keyId);

	  // Decrypt the response.
	  DecryptResponse response = client.decrypt(keyName, ByteString.copyFrom(ciphertext));
	  System.out.printf("Plaintext: %s%n", response.getPlaintext().toStringUtf8());
	}
  }
}
