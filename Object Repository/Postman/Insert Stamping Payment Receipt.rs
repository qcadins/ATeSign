<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Insert Stamping Payment Receipt</name>
   <tag></tag>
   <elementGuidId>9149eb1c-2162-4ce1-aa13-2457fcc82388</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>false</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n    \&quot;audit\&quot;: {\n        \&quot;callerId\&quot;: \&quot;${callerId}\&quot;\n    },\n    \&quot;documentTemplateCode\&quot;: \&quot;${documentTemplateCode}\&quot;,\n    \&quot;documentNumber\&quot;: \&quot;${documentNumber}\&quot;,\n    \&quot;documentTransactionId\&quot;: \&quot;${documentTransactionId}\&quot;,\n    \&quot;docName\&quot;: \&quot;${docName}\&quot;,\n    \&quot;docDate\&quot;: \&quot;${docDate}\&quot;,\n    \&quot;docType\&quot;: \&quot;${docType}\&quot;,\n    \&quot;docNominal\&quot;: \&quot;${docNominal}\&quot;,\n    \&quot;peruriDocTypeId\&quot;: \&quot;${peruriDocTypeId}\&quot;,\n    \&quot;officeCode\&quot;: \&quot;${officeCode}\&quot;,\n    \&quot;officeName\&quot;: \&quot;${officeName}\&quot;,\n    \&quot;regionCode\&quot;: \&quot;${regionCode}\&quot;,\n    \&quot;regionName\&quot;: \&quot;${regionName}\&quot;,\n    \&quot;idType\&quot;: \&quot;${idType}\&quot;,\n    \&quot;idNo\&quot;: \&quot;${idNo}\&quot;,\n    \&quot;taxOwedsName\&quot;: \&quot;${taxOwedsName}\&quot;,\n    \&quot;returnStampResult\&quot;: \&quot;${returnStampResult}\&quot;,\n    \&quot;documentFile\&quot;: \&quot;${documentFile}\&quot;\n}&quot;,
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
      <webElementGuid>3feee6d4-a6ea-47e7-a4be-2c75c237645e</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>46b37abd-d734-42a9-9413-edc63381406d</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>dba3513c-6795-440c-ab25-cb2159b2c5c0</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/confins/stampduty/insertStampingPaymentReceipt</restUrl>
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
      <id>8eb1a54a-ba01-45d0-a545-d2e5722ddea8</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.api_key</defaultValue>
      <description></description>
      <id>0fb6d1f3-e4d2-4aef-95ec-027c02132cb2</id>
      <masked>false</masked>
      <name>api_key</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.Tenant</defaultValue>
      <description></description>
      <id>1323c4a5-afab-4e42-b98c-2101c4eb4d0c</id>
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
