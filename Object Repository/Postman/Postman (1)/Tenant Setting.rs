<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Tenant Setting</name>
   <tag></tag>
   <elementGuidId>3a836362-6ef8-444b-bf05-148ad2c0349e</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n  \&quot;audit\&quot;: {\r\n    \&quot;callerId\&quot;: \&quot;ADMESIGN\&quot;\r\n  },\r\n   \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;emailReminderDest\&quot;: [\&quot;ANDY@AD-INS.COM\&quot;],\r\n    \&quot;refNumberLabel\&quot;: \&quot;No Kontrak : \&quot;,\r\n    \&quot;thresholdBalance\&quot;: {\r\n        \&quot;OTP\&quot;: 5000,\r\n        \&quot;VRF\&quot;: 1000,\r\n        \&quot;DOC\&quot;: 3000,\r\n        \&quot;SDT\&quot;: 5000,\r\n        \&quot;SMS\&quot;: 5000,\r\n        \&quot;SGN\&quot;: 0\r\n    }\r\n}&quot;,
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
      <webElementGuid>a33704d7-be05-4003-8170-52af2f37e938</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>78d84f33-b44a-4f25-ba43-17a875407217</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer 51404ebe-2a9a-4d72-8a1c-2077a5ccbfef</value>
      <webElementGuid>1c295306-5db2-47e4-80ac-8c5c9d4983f2</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/tenant/s/updateTenantSettings</restUrl>
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
      <id>a15c3dc1-1d18-47e6-8260-5ae8606c036a</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
