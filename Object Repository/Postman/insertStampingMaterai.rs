<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>insertStampingMaterai</name>
   <tag></tag>
   <elementGuidId>b4a864b7-1a65-416b-aaf0-9e1bc53b8698</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>false</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n    \&quot;audit\&quot;: {\n        \&quot;callerId\&quot;: \&quot;${callerId}\&quot;\n    },\n    \&quot;documentTemplateCode\&quot;: \&quot;${documentTemplateCode}\&quot;,\n    \&quot;documentNumber\&quot;: \&quot;${documentNumber}\&quot;,\n    \&quot;docName\&quot;: \&quot;${docName}\&quot;,\n    \&quot;docDate\&quot;: \&quot;${docDate}\&quot;,\n    \&quot;docTypeCode\&quot;: \&quot;${docTypeCode}\&quot;,\n    \&quot;peruriDocTypeId\&quot;: \&quot;${peruriDocTypeId}\&quot;,\n    \&quot;docNominal\&quot;: \&quot;${docNominal}\&quot;,\n    \&quot;taxType\&quot;: \&quot;${taxType}\&quot;,\n    \&quot;idType\&quot;: \&quot;${idType}\&quot;,\n    \&quot;idNo\&quot;: \&quot;${idNo}\&quot;,\n    \&quot;taxOwedsName\&quot;: \&quot;${taxOwedsName}\&quot;,\n    \&quot;officeCode\&quot;: \&quot;${officeCode}\&quot;,\n    \&quot;officeName\&quot;: \&quot;${officeName}\&quot;,\n    \&quot;regionCode\&quot;: \&quot;${regionCode}\&quot;,\n    \&quot;regionName\&quot;: \&quot;${regionName}\&quot;,\n    \&quot;businessLineCode\&quot;: \&quot;${businessLineCode}\&quot;,\n    \&quot;businessLineName\&quot;: \&quot;${businessLineName}\&quot;,\n    \&quot;stampingLocations\&quot;: [${stampingLocations}],\n    \&quot;documentFile\&quot;: \&quot;${documentFile}\&quot;\n}&quot;,
  &quot;contentType&quot;: &quot;application/json&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>${api_key}@${tenant_code}</value>
      <webElementGuid>8f0c1cae-25b0-4f1a-b9e3-c0b1a6a36c09</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>20e73358-f65f-4743-a1d3-a1e0a192d4c9</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/external/document/insertStampingMaterai</restUrl>
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
      <id>35e68192-c5a8-48ce-b0b0-84eec64f8078</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.api_key</defaultValue>
      <description></description>
      <id>98964eeb-3320-4a7e-a376-f69a03a051b2</id>
      <masked>false</masked>
      <name>api_key</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.Tenant</defaultValue>
      <description></description>
      <id>e4a483f7-b9ba-45d0-bf26-50c567062596</id>
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
