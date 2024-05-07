<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Request Link Asal</name>
   <tag></tag>
   <elementGuidId>df674be6-9b2a-4d3a-8989-ef78a40661bc</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;mobile\&quot;\r\n    },\r\n    \&quot;tenantCode\&quot;: \&quot;ADINS\&quot;,\r\n    \&quot;users\&quot;: [\r\n        {\r\n            \&quot;nama\&quot;: \&quot;USERBDAJ\&quot;,\r\n            \&quot;tlp\&quot;: \&quot;001000991309\&quot;,\r\n            \&quot;email\&quot;: \&quot;VIDA.BDAJ@ESIGNHUB.MY.ID\&quot;,\r\n            \&quot;jenisKelamin\&quot;: \&quot;F\&quot;,\r\n            \&quot;tmpLahir\&quot;: \&quot;BOGOR\&quot;,\r\n            \&quot;tglLahir\&quot;: \&quot;1980-01-01\&quot;,\r\n            \&quot;idKtp\&quot;: \&quot;3511000101801309\&quot;,\r\n            \&quot;provinsi\&quot;: \&quot;Jawa Barat\&quot;,\r\n            \&quot;kota\&quot;: \&quot;Bogor\&quot;,\r\n            \&quot;kecamatan\&quot;: \&quot;Bogor Selatan\&quot;,\r\n            \&quot;kelurahan\&quot;: \&quot;Baranangsiang\&quot;,\r\n            \&quot;kodePos\&quot;: \&quot;16143\&quot;,\r\n            \&quot;alamat\&quot;: \&quot;JL. SAWOKNA NO.1000 BANTAR KEMANG\&quot;\r\n        }\r\n    ]\r\n}&quot;,
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
      <webElementGuid>5d552cd5-5192-44a4-8d24-26b175b312bd</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@WOMF</value>
      <webElementGuid>aee452a0-673c-48f3-a99f-b526b0f3c06a</webElementGuid>
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
      <id>b53c676a-4841-484e-b28a-cc3dc2550045</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
