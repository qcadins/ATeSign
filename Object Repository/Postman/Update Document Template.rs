<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Update Document Template</name>
   <tag></tag>
   <elementGuidId>acc8b4e6-52c8-4a83-8049-d57b9010dc55</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>false</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n    \&quot;documentExample\&quot;: \&quot;${documentExample}\&quot;,\n    \&quot;documentTemplateCode\&quot;: \&quot;${documentTemplateCode}\&quot;,\n    \&quot;documentTemplateName\&quot;: \&quot;${documentTemplateName}\&quot;,\n    \&quot;documentTemplateDescription\&quot;: \&quot;${documentTemplateDescription}\&quot;,\n    \&quot;isActive\&quot;: \&quot;${isActive}\&quot;,\n    \&quot;isSequence\&quot;: \&quot;${isSequence}\&quot;,\n    \&quot;numberOfPage\&quot;: ${numberOfPage},\n    \&quot;paymentSignTypeCode\&quot;: \&quot;${paymentSignTypeCode}\&quot;,\n    \&quot;tenantCode\&quot;: \&quot;${tenantCode}\&quot;,\n    \&quot;vendorCode\&quot;: \&quot;${vendorCode}\&quot;,\n    \&quot;isSignLocOnly\&quot;: \&quot;${isSignLocOnly}\&quot;,\n    \&quot;signer\&quot;: [\n        ${signer}\n    ],\n    \&quot;audit\&quot;: {\n        \&quot;callerId\&quot;: \&quot;${callerId}\&quot;\n    }\n}&quot;,
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
      <webElementGuid>458e97e8-a9ee-47fc-aa0f-c9364c8fbbf8</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept-Language</name>
      <type>Main</type>
      <value>en-US,en;q=0.9</value>
      <webElementGuid>b045e5e7-1dc5-4e48-8a2f-7aec2b273298</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer ${token}</value>
      <webElementGuid>d403fc83-31f7-4a6f-a354-ab2ebf068843</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Connection</name>
      <type>Main</type>
      <value>keep-alive</value>
      <webElementGuid>c37cc895-11f9-4ed5-97c4-184c128ba480</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>41bc93a0-a77b-4750-97ac-09c45f5e6dd9</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/document/s/updateDocumentTemplate</restUrl>
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
      <id>44becd3e-0cbe-4d3f-a648-e6f9a5e5e6ff</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.token</defaultValue>
      <description></description>
      <id>89653622-8ab9-41e0-86e0-150cfcb2df8e</id>
      <masked>false</masked>
      <name>token</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.Tenant</defaultValue>
      <description></description>
      <id>eb95db16-3b9c-4753-8fba-8e279657cb57</id>
      <masked>false</masked>
      <name>tenantCode</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.Psre</defaultValue>
      <description></description>
      <id>052ef91e-3a3b-479f-ac9e-3a94f0067703</id>
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
