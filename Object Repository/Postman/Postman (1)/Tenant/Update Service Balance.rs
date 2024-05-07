<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Update Service Balance</name>
   <tag></tag>
   <elementGuidId>40ff5918-36e7-445c-ad82-baba00c4f4f2</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;ADMESIGN\&quot;\r\n    },\r\n    \&quot;tenantCode\&quot; : \&quot;TESFE1\&quot;,\r\n    \&quot;listBalanceTenant\&quot;: [\r\n        {\r\n            \&quot;vendorCode\&quot; : \&quot;DIGI\&quot;,\r\n            \&quot;balanceTypeCode\&quot; : \&quot;OTP\&quot;,\r\n            \&quot;isActive\&quot; : \&quot;1\&quot;\r\n        },\r\n        {\r\n            \&quot;vendorCode\&quot; : \&quot;ESG\&quot;,\r\n            \&quot;balanceTypeCode\&quot; : \&quot;SMS\&quot;,\r\n            \&quot;isActive\&quot; : \&quot;1\&quot;\r\n        },\r\n        {\r\n            \&quot;vendorCode\&quot; : \&quot;DIGI\&quot;,\r\n            \&quot;balanceTypeCode\&quot; : \&quot;DOC\&quot;,\r\n            \&quot;isActive\&quot; : \&quot;1\&quot;\r\n        },\r\n        {\r\n            \&quot;vendorCode\&quot; : \&quot;ESG\&quot;,\r\n            \&quot;balanceTypeCode\&quot; : \&quot;FVRF\&quot;,\r\n            \&quot;isActive\&quot; : \&quot;1\&quot;\r\n        },\r\n        {\r\n            \&quot;vendorCode\&quot; : \&quot;ESG\&quot;,\r\n            \&quot;balanceTypeCode\&quot; : \&quot;SDT\&quot;,\r\n            \&quot;isActive\&quot; : \&quot;1\&quot;\r\n        },\r\n        {\r\n            \&quot;vendorCode\&quot; : \&quot;DIGI\&quot;,\r\n            \&quot;balanceTypeCode\&quot; : \&quot;DOC\&quot;,\r\n            \&quot;isActive\&quot; : \&quot;1\&quot;\r\n        }\r\n    ]\r\n\r\n}\r\n&quot;,
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
      <webElementGuid>04422935-3672-41cc-ab80-dfcd7827bcc5</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>67a4ee72-c4db-4bb3-9a69-c0083d80a976</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer 9fab49d5-9d1d-4424-9a29-0ddf3b37842d</value>
      <webElementGuid>e40a89ff-942f-4480-9576-2612be8db333</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/saldo/s/updateBalanceTenant</restUrl>
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
      <id>ae1ee00e-ee81-4a43-9532-ace4b85eebfa</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
