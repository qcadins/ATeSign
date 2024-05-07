<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Request Link VIDA UAT Copy</name>
   <tag></tag>
   <elementGuidId>eb6e7343-2a89-4c56-9c49-8b33142b0374</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;mobile\&quot;\r\n    },\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;users\&quot;: [\r\n        {\r\n            \&quot;nama\&quot;: \&quot;Andy\&quot;,\r\n            \&quot;tlp\&quot;: \&quot;089693475025\&quot;,\r\n            \&quot;email\&quot;: \&quot;\&quot;,\r\n            \&quot;jenisKelamin\&quot;: \&quot;M\&quot;,\r\n            \&quot;tmpLahir\&quot;: \&quot;BOGOR\&quot;,\r\n            \&quot;tglLahir\&quot;: \&quot;1991-12-13\&quot;,\r\n            \&quot;idKtp\&quot;: \&quot;3271011312910014\&quot;,\r\n            \&quot;provinsi\&quot;: \&quot;Jawa Barat\&quot;,\r\n            \&quot;kota\&quot;: \&quot;Bogor\&quot;,\r\n            \&quot;kecamatan\&quot;: \&quot;Bogor Selatan\&quot;,\r\n            \&quot;kelurahan\&quot;: \&quot;Cipaku\&quot;,\r\n            \&quot;kodePos\&quot;: \&quot;16143\&quot;,\r\n            \&quot;alamat\&quot;: \&quot;Cipaku Indah I Blok G No.17\&quot;\r\n        }\r\n    ]\r\n}&quot;,
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
      <webElementGuid>b3182e26-0c17-419d-b9e0-6a94c6744403</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@WOMF</value>
      <webElementGuid>7d5ecdb9-d6e4-4ee4-8772-128a722a3cd2</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${uat_url}/services/user/generateInvitationLink</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.uat_url</defaultValue>
      <description></description>
      <id>004e6fa5-22c9-4b18-bb08-d73c4ac07fec</id>
      <masked>false</masked>
      <name>uat_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
