<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Request Link Email Copy</name>
   <tag></tag>
   <elementGuidId>5237c889-97f7-4c76-8c99-29324781c77f</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;users\&quot;: [\r\n        {\r\n            \&quot;nama\&quot;: \&quot;ANDY\&quot;,\r\n            \&quot;email\&quot;: \&quot;emailkosong0@esignhub.my.id\&quot;,\r\n            \&quot;vendorCode\&quot;: \&quot;TKNAJ\&quot;\r\n        }\r\n    ],\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;MANUAL\&quot;\r\n    }\r\n}&quot;,
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
      <webElementGuid>0ff5f80e-36c5-4db8-bbc2-507ff6955e9d</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@WOMF</value>
      <webElementGuid>5bdcac90-2ad1-4e2c-a148-3bdeb71b8dc5</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${local_url}/services/user/generateInvitationLinkV2</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.local_url</defaultValue>
      <description></description>
      <id>e44d1a91-a5a9-408e-8615-9c39b1f01d0c</id>
      <masked>false</masked>
      <name>local_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
