<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Submit Result Rekon</name>
   <tag></tag>
   <elementGuidId>6b9eed24-456f-4ba7-9e3a-3665f6a84a8b</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>false</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\&quot;vendorCode\&quot;:\&quot;${vendorCode}\&quot;,\&quot;tenantCode\&quot;:\&quot;${tenantCode}\&quot;,\&quot;balanceType\&quot;:\&quot;${balanceType}\&quot;,\&quot;startDate\&quot;:\&quot;${startDate}\&quot;,\&quot;endDate\&quot;:\&quot;${endDate}\&quot;,\&quot;loginId\&quot;:\&quot;${loginId}\&quot;,\&quot;audit\&quot;:{\&quot;callerId\&quot;:\&quot;${callerId}\&quot;}}&quot;,
  &quot;contentType&quot;: &quot;application/json&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json, text/plain, */*</value>
      <webElementGuid>aeb03847-406f-436d-ad4b-3282df5381a0</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer ${token}</value>
      <webElementGuid>a61ce88d-0342-478a-b712-feae6d5b6150</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Cache-Control</name>
      <type>Main</type>
      <value>no-cache</value>
      <webElementGuid>eb679263-3321-44f4-a797-2ff457cb3be2</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Connection</name>
      <type>Main</type>
      <value>keep-alive</value>
      <webElementGuid>89135d76-e7f0-40e8-8f8c-cb9453fe7637</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>0da40de6-67e2-4cda-a237-e7e58ce1f88a</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Pragma</name>
      <type>Main</type>
      <value>no-cache</value>
      <webElementGuid>890cdd3d-3f8b-4def-83cf-5460c467ccce</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/jobResult/s/submitResultRekon</restUrl>
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
      <id>a4eeb4a2-a8c9-49ff-b411-65c621afe362</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.token</defaultValue>
      <description></description>
      <id>ec011ab1-47de-482d-a17d-e8bade8a0d9a</id>
      <masked>false</masked>
      <name>token</name>
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
