<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Request Link Copy 2</name>
   <tag></tag>
   <elementGuidId>bcb906d8-7cf9-4879-8630-fc7587a70b8a</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;mobile\&quot;\r\n    },\r\n    \&quot;tenantCode\&quot;: \&quot;ADINS\&quot;,\r\n    \&quot;users\&quot;: [\r\n        {\r\n            \&quot;nama\&quot;:\&quot;Andy\&quot;,\r\n            \&quot;tlp\&quot; : \&quot;081117162134\&quot;,\r\n            \&quot;tglLahir\&quot; : \&quot;\&quot;,\r\n            \&quot;idKtp\&quot; : \&quot;31720164089100088\&quot;,\r\n            \&quot;email\&quot; : \&quot;andy_s17@esignhub.my.id\&quot;\r\n\r\n\r\n        }\r\n        \r\n        \r\n    ]\r\n}\r\n&quot;,
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
      <webElementGuid>6f07be09-3341-4efd-8174-1f8bfbd152f2</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@ADIRA</value>
      <webElementGuid>0a1a13e8-a7c8-4e50-9998-c11f0d20ca69</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/user/generateInvitationLink</restUrl>
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
      <id>09d02aab-c636-41b1-8fe3-cb8aa9151c31</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
