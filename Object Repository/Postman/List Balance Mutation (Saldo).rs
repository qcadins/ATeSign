<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>List Balance Mutation (Saldo)</name>
   <tag></tag>
   <elementGuidId>4100fc11-5ae7-4bd1-b481-561497f6ebde</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <authorizationRequest>
      <authorizationInfo>
         <entry>
            <key>bearerToken</key>
            <value>${token}</value>
         </entry>
      </authorizationInfo>
      <authorizationType>Bearer</authorizationType>
   </authorizationRequest>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n    \&quot;tenantCode\&quot;: \&quot;${tenantCode}\&quot;,\n    \&quot;vendorCode\&quot;: \&quot;${vendorCode}\&quot;,\n    \&quot;audit\&quot;: {\n        \&quot;callerId\&quot;: \&quot;${callerId}\&quot;\n    },\n    \&quot;page\&quot;: \&quot;${pageNum}\&quot;,\n    \&quot;balanceType\&quot;: \&quot;${balType}\&quot;,\n    \&quot;referenceNo\&quot;: \&quot;${refNo}\&quot;,\n    \&quot;transactionType\&quot;: \&quot;${tranxType}\&quot;,\n    \&quot;documentName\&quot;: \&quot;${docname}\&quot;,\n    \&quot;transactionDateStart\&quot;: \&quot;${tranxDateStart}\&quot;,\n    \&quot;transactionDateEnd\&quot;: \&quot;${tranxDateEnd}\&quot;,\n    \&quot;documentType\&quot;: \&quot;${docType}\&quot;,\n  \t\&quot;officeCode\&quot;: \&quot;${officeCode}\&quot;\n}&quot;,
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
      <webElementGuid>e4552c65-9e0d-4afe-b2a2-00349162e6b8</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept-Language</name>
      <type>Main</type>
      <value>id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7</value>
      <webElementGuid>a05ba669-e024-45fd-bcd9-5faa766b4558</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Connection</name>
      <type>Main</type>
      <value>keep-alive</value>
      <webElementGuid>c86e6537-de26-47ee-b96d-887b5ac6258a</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>9ee5b469-2dc0-4bee-9ff0-05e9505f9c56</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>DNT</name>
      <type>Main</type>
      <value>1</value>
      <webElementGuid>09d66602-cd47-4600-bb1e-5ec19cfc89c0</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Origin</name>
      <type>Main</type>
      <value>http://gdkwebsvr:8080</value>
      <webElementGuid>0418ec99-e19f-49a1-ae9d-de2449f7f3c7</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Referer</name>
      <type>Main</type>
      <value>http://gdkwebsvr:8080/</value>
      <webElementGuid>96ef1dda-34c7-463b-96ac-8a65bf1095ec</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>User-Agent</name>
      <type>Main</type>
      <value>Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36</value>
      <webElementGuid>7a002d81-751a-418b-b00f-c75402fd02d5</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer ${token}</value>
      <webElementGuid>7e1ee2e7-349e-455b-8351-86cce102f299</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.5.5</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/saldo/s/listBalanceMutation</restUrl>
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
      <id>1157c363-9c89-4218-9886-f97d40b494d0</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.token</defaultValue>
      <description></description>
      <id>5a8a2041-3fd5-47e3-910b-3ff128d421e0</id>
      <masked>false</masked>
      <name>token</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.Tenant</defaultValue>
      <description></description>
      <id>f4d83f0c-a6a2-4e97-902c-8e39312c4657</id>
      <masked>false</masked>
      <name>tenantCode</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.Psre</defaultValue>
      <description></description>
      <id>5ba7aafe-03ff-4d48-adec-be7fe92f35b0</id>
      <masked>false</masked>
      <name>vendorCode</name>
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
