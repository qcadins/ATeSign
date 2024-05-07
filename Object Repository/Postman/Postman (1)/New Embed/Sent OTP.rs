<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Sent OTP</name>
   <tag></tag>
   <elementGuidId>aca44373-9571-4711-a13d-f7b4ceafc11d</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;msg\&quot;: \&quot;jJwIXY4SY5WmgDDCFMaeezsXZMLFVqawcPY2Iu6jc4mostMBQ2Ob7ZahO5sGnTKC3l7mFz1R1MoVOfOG8N%2BZ%2Bg%3D%3D\&quot;,\r\n    \&quot;tenantCode\&quot;: \&quot;ADINS\&quot;,\r\n    \&quot;vendorCode\&quot; : \&quot;VIDA\&quot;,\r\n    \&quot;phoneNo\&quot;: \&quot;001000990221\&quot;,\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;VIDA.ACCB@ESIGNHUB.MY.ID\&quot;\r\n    }\r\n}&quot;,
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
      <webElementGuid>46abdd33-c40a-4b45-a823-c1b7a2ee8001</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>39cdd829-c278-4130-a647-db777abde644</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/embed/user/sentOtpSigningEmbed</restUrl>
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
      <id>d88d4d89-e96a-4872-96cc-68843967f6d9</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
