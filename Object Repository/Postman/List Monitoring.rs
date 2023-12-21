<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>List Monitoring</name>
   <tag></tag>
   <elementGuidId>f3a982f5-61f3-4539-86e3-44000fdc659a</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>false</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\&quot;tenantCode\&quot;:\&quot;${tenantCode}\&quot;,\&quot;page\&quot;:${page},\&quot;nomorDokumen\&quot;:\&quot;${nomorDokumen}\&quot;,\&quot;jenisDokumen\&quot;:\&quot;${jenisDokumen}\&quot;,\&quot;tipeDokumen\&quot;:\&quot;${tipeDokumen}\&quot;,\&quot;templateDokumen\&quot;:\&quot;${templateDokumen}\&quot;,\&quot;tanggalDokumenMulai\&quot;:\&quot;${tanggalDokumenMulai}\&quot;,\&quot;tanggalDokumenSampai\&quot;:\&quot;${tanggalDokumenSampai}\&quot;,\&quot;hasilStamping\&quot;:\&quot;${hasiLStamping}\&quot;,\&quot;noSN\&quot;:\&quot;${noSN}\&quot;,\&quot;cabang\&quot;:\&quot;${cabang}\&quot;,\&quot;taxType\&quot;:\&quot;${taxType}\&quot;,\&quot;audit\&quot;:{\&quot;callerId\&quot;:\&quot;${callerId}\&quot;}}&quot;,
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
      <webElementGuid>9f366072-26f7-40bc-a1b7-9bfcfa96749e</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept-Language</name>
      <type>Main</type>
      <value>en-US,en;q=0.9</value>
      <webElementGuid>961303aa-db37-4c9f-91ec-2c7fd948fe3d</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer ${token}</value>
      <webElementGuid>d47e063b-3d14-49d0-9b94-f0585b666ccb</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Connection</name>
      <type>Main</type>
      <value>keep-alive</value>
      <webElementGuid>9a851fd2-ebd5-4aa4-a4b1-06552081a9ab</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>c4785c73-d523-442a-a2e8-6ae34304c2d6</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/stampduty/s/listMonitoring</restUrl>
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
      <id>46f43ed0-2ce2-430a-9d98-949d6dfd5285</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.token</defaultValue>
      <description></description>
      <id>ed9d0ee7-4392-4ec3-be83-2544556aa727</id>
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
