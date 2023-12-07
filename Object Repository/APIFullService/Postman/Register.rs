<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Register</name>
   <tag></tag>
   <elementGuidId>bfd654fc-5a9a-4e08-b532-2c35aa9a77ef</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n    \&quot;audit\&quot;: {\n        \&quot;callerId\&quot;: \&quot;${callerId}\&quot;\n    },\n    \&quot;psreCode\&quot;: \&quot;${psreCode}\&quot;,\n\t\&quot;nama\&quot;: \&quot;${nama}\&quot;,\n    \&quot;email\&quot;: \&quot;${email}\&quot;,\n    \&quot;tmpLahir\&quot;: \&quot;${tmpLahir}\&quot;,\n    \&quot;tglLahir\&quot;: \&quot;${tglLahir}\&quot;,\n    \&quot;jenisKelamin\&quot;: \&quot;${jenisKelamin}\&quot;,\n    \&quot;tlp\&quot;: \&quot;${tlp}\&quot;,\n    \&quot;idKtp\&quot;: \&quot;${idKtp}\&quot;,\n    \&quot;alamat\&quot;: \&quot;${alamat}\&quot;,\n    \&quot;kecamatan\&quot;: \&quot;${kecamatan}\&quot;,\n    \&quot;kelurahan\&quot;: \&quot;${kelurahan}\&quot;,\n    \&quot;kota\&quot;: \&quot;${kota}\&quot;,\n    \&quot;provinsi\&quot;: \&quot;${provinsi}\&quot;,\n    \&quot;kodePos\&quot;: \&quot;${kodePos}\&quot;,\n    \&quot;selfPhoto\&quot;: \&quot;${selfPhoto}\&quot;,\n    \&quot;idPhoto\&quot;: \&quot;${idPhoto}\&quot;,\n    \&quot;password\&quot;: \&quot;${password}\&quot;\n}\n&quot;,
  &quot;contentType&quot;: &quot;application/json&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>1c4926d0-8447-40d1-92bc-507c53f76002</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>06968266-469c-4ca8-8e18-9f62db0e7498</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>${api_key}@${tenant_code}</value>
      <webElementGuid>8f0317fc-1f29-4909-a70e-1bb3e033f247</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.5.5</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/external/user/register</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.base_url</defaultValue>
      <description></description>
      <id>45248845-b2a3-42ff-a1a5-f1c525d2f959</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.api_key</defaultValue>
      <description></description>
      <id>06bec374-a825-4f7c-a2d7-16124b8d7847</id>
      <masked>false</masked>
      <name>api_key</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.Tenant</defaultValue>
      <description></description>
      <id>dfe4c694-3be4-44ab-92f8-d8d9e5da661a</id>
      <masked>false</masked>
      <name>tenant_code</name>
   </variables>
   <verificationScript>import static org.assertj.core.api.Assertions.*

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.verification.WSResponseManager

import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable

RequestObject request = WSResponseManager.getInstance().getCurrentRequest()

ResponseObject response = WSResponseManager.getInstance().getCurrentResponse()</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
