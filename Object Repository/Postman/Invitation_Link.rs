<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Invitation_Link</name>
   <tag></tag>
   <elementGuidId>54a08ce5-a590-48a4-856d-2170d60719ce</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;mobile\&quot;\r\n    },\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;users\&quot;: [\r\n        {\r\n            \&quot;nama\&quot;:\&quot;TEST DIRECT\&quot;,\r\n            \&quot;email\&quot; : \&quot;tes.script@andyresearch.my.id\&quot;,\r\n            \&quot;tlp\&quot; : \&quot;0812321456781\&quot;\r\n        },\r\n        {\r\n            \&quot;nama\&quot;:\&quot;TEST DIRECT\&quot;,\r\n            \&quot;email\&quot; : \&quot;tes.script2@andyresearch.my.id\&quot;,\r\n            \&quot;tlp\&quot; : \&quot;081232145678\&quot;\r\n        }\r\n        \r\n        \r\n    ]\r\n}\r\n&quot;,
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
      <webElementGuid>ac1b274d-beff-4b36-9003-c6f0d9319c76</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@ADIRA</value>
      <webElementGuid>126edc16-faca-4106-8b50-0c1f188b934d</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.5.5</katalonVersion>
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
      <id>cec17119-f3c0-4ada-89fd-02bb9e45c85e</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>