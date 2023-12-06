<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Generate Invitation Link</name>
   <tag></tag>
   <elementGuidId>9841a340-b5df-4328-8da4-3b3fe91cfded</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n\t\&quot;nama\&quot;: \&quot;${nama}\&quot;,\n\t\&quot;email\&quot;: \&quot;${email}\&quot;,\n    \&quot;tmpLahir\&quot;: \&quot;${tmpLahir}\&quot;,\n\t\&quot;tglLahir\&quot;: \&quot;${tglLahir}\&quot;,    \n\t\&quot;jenisKelamin\&quot;: \&quot;${jenisKelamin}\&quot;,\n\t\&quot;tlp\&quot;: \&quot;${tlp}\&quot;,\n    \&quot;idKtp\&quot;: \&quot;${idKtp}\&quot;,\n\t\&quot;alamat\&quot;: \&quot;${alamat}\&quot;,\n    \&quot;kecamatan\&quot;: \&quot;${kecamatan}\&quot;,\n    \&quot;kelurahan\&quot;: \&quot;${kelurahan}\&quot;,\n\t\&quot;kota\&quot;: \&quot;${kota}\&quot;,\n\t\&quot;provinsi\&quot;: \&quot;${provinsi}\&quot;,\n    \&quot;kodePos\&quot;: \&quot;${kodePos}\&quot;,    \n    \&quot;selfPhoto\&quot;: \&quot;${selfPhoto}\&quot;,\n    \&quot;idPhoto\&quot;: \&quot;${idPhoto}\&quot;,\n\t\&quot;type\&quot;: \&quot;${type}\&quot;,\n    \&quot;region\&quot;: \&quot;${region}\&quot;,\n    \&quot;office\&quot;: \&quot;${office}\&quot;,\n    \&quot;businessLine\&quot;: \&quot;${businessLine}\&quot;,\n    \&quot;taskNo\&quot;: \&quot;${taskNo}\&quot;,\n    \&quot;audit\&quot;: {\n        \&quot;callerId\&quot;: \&quot;${callerId}\&quot;\n    }\n}&quot;,
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
      <webElementGuid>b506ae20-f229-4c81-b71c-74c1ad3e4406</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>c318daee-44b7-4172-97c0-453f6c4807ac</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>${api_key}@${tenant_code}</value>
      <webElementGuid>4495eafe-8dcb-4e42-8129-3767f1ee425d</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.5.5</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/external/user/generateInvLink</restUrl>
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
      <id>402c0051-939d-44a4-9225-d415c3e8aebb</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.api_key</defaultValue>
      <description></description>
      <id>02ec9295-242f-47db-a840-6559b9400d05</id>
      <masked>false</masked>
      <name>api_key</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.Tenant</defaultValue>
      <description></description>
      <id>1994fbd2-533f-4e51-b8dc-c75e4f1b3356</id>
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
