<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Edit Tenant</name>
   <tag></tag>
   <elementGuidId>fa5a558b-cfa3-4b88-a8d2-d0f5a288a471</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;ADMESIGN\&quot;\r\n    },\r\n    \&quot;emailReminderDest\&quot;: [\&quot;\&quot;],\r\n    \&quot;tenantCode\&quot;: \&quot;TENANT TEST 1\&quot;,\r\n    \&quot;tenantName\&quot;: \&quot;TENANT TEST EDIT 1\&quot;,\r\n    \&quot;apiKey\&quot;: \&quot;123\&quot;,\r\n    \&quot;thresholdBalance\&quot;: {\r\n        \&quot;OTP\&quot;: 1,\r\n        \&quot;VRF\&quot;: 1,\r\n        \&quot;DOC\&quot;: 1,\r\n        \&quot;SDT\&quot;: 1,\r\n        \&quot;SMS\&quot;: 1,\r\n        \&quot;FVRF\&quot;: 1,\r\n        \&quot;SGN\&quot;: 1\r\n    },\r\n    \&quot;refNumberLabel\&quot;: \&quot;Nomor Kontrak 2\&quot;,\r\n    \&quot;emailUserAdmin\&quot;: \&quot;ADMIN@TENANTTEST3\&quot;,\r\n    \&quot;passwordUserAdmin\&quot;: \&quot;123\&quot;\r\n}\r\n&quot;,
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
      <webElementGuid>f0df2004-2194-4fff-8d5f-0b6f2a4ba109</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>57df6b61-9cd2-479f-a9b8-dd245b1309c0</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer 5fe3a6c5-1923-4977-9b73-b8b96c022fc7</value>
      <webElementGuid>36734098-f4f8-43bc-901f-427193c861e7</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/tenant/s/editTenant</restUrl>
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
      <id>21e7e9bc-9b05-49d9-a645-96f811506a0b</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
