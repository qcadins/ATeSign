<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Request Link VIDA 3</name>
   <tag></tag>
   <elementGuidId>5a1a7e5d-2e90-4546-9637-e37e4958d0c7</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;mobile\&quot;\r\n    },\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;users\&quot;: [\r\n        {\r\n            \&quot;nama\&quot;: \&quot;UserBDAH\&quot;,\r\n            \&quot;tlp\&quot;: \&quot;081000991307\&quot;,\r\n            \&quot;email\&quot;: \&quot;\&quot;,\r\n            \&quot;jenisKelamin\&quot;: \&quot;F\&quot;,\r\n            \&quot;tmpLahir\&quot;: \&quot;BOGOR\&quot;,\r\n            \&quot;tglLahir\&quot;: \&quot;1980-01-01\&quot;,\r\n            \&quot;idKtp\&quot;: \&quot;3521000101801307\&quot;,\r\n            \&quot;provinsi\&quot;: \&quot;Jawa Barat\&quot;,\r\n            \&quot;kota\&quot;: \&quot;Bogor\&quot;,\r\n            \&quot;kecamatan\&quot;: \&quot;Bogor Selatan\&quot;,\r\n            \&quot;kelurahan\&quot;: \&quot;Baranangsiang\&quot;,\r\n            \&quot;kodePos\&quot;: \&quot;16143\&quot;,\r\n            \&quot;alamat\&quot;: \&quot;JL. SAWOKNA NO.1000 BANTAR KEMANG\&quot;\r\n        }\r\n    ]\r\n}&quot;,
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
      <webElementGuid>05ae2b6d-ec52-4ec2-a8a3-d5395a9dd8a2</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@WOMF</value>
      <webElementGuid>770dbe6f-40a9-42dd-8d55-957873473085</webElementGuid>
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
      <id>7db79664-11e9-4de9-9eb6-36add22beeeb</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
