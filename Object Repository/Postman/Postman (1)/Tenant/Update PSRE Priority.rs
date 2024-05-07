<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Update PSRE Priority</name>
   <tag></tag>
   <elementGuidId>6c90ec17-44ff-4942-954e-6fa765509aa5</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;admin@adins.co.id\&quot;\r\n    },\r\n    \&quot;vendor\&quot;: [\r\n        {\r\n            \&quot;vendorCode\&quot;: \&quot;VIDA\&quot;,\r\n            \&quot;priority\&quot;: \&quot;1\&quot;\r\n        },\r\n        {\r\n            \&quot;vendorCode\&quot;: \&quot;PRIVY\&quot;,\r\n            \&quot;priority\&quot;: \&quot;2\&quot;\r\n        },\r\n        {\r\n            \&quot;vendorCode\&quot;: \&quot;DIGI\&quot;,\r\n            \&quot;priority\&quot;: \&quot;3\&quot;\r\n        }\r\n    ],\r\n    \&quot;tenantCode\&quot;: \&quot;adins\&quot;\r\n}&quot;,
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
      <webElementGuid>07e6ee06-734e-43fb-a899-daaa54a0ad8f</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>bearer ${token}</value>
      <webElementGuid>1247889a-32ac-4f9a-bbac-bc3df77215a1</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/vendor/s/updatePriorityPSrE</restUrl>
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
      <id>54c88280-dd9c-4982-8141-646da9195512</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.token</defaultValue>
      <description></description>
      <id>909a2dbd-44a4-4fa3-aced-f4ec66ac2a93</id>
      <masked>false</masked>
      <name>token</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
