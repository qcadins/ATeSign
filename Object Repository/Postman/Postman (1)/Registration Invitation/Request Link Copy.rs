<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Request Link Copy</name>
   <tag></tag>
   <elementGuidId>14ea0baa-8a89-4274-9c90-7440bbe123be</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;users\&quot;: [\r\n        {\r\n            \&quot;alamat\&quot;: \&quot;Jalan Alamat 12982317\&quot;,\r\n            \&quot;jenisKelamin\&quot;: \&quot;M\&quot;,\r\n            \&quot;kecamatan\&quot;: \&quot;Kebon Jeruk\&quot;,\r\n            \&quot;kelurahan\&quot;: \&quot;Sukabumi Selatan (Udik)\&quot;,\r\n            \&quot;kodePos\&quot;: \&quot;11560\&quot;,\r\n            \&quot;kota\&quot;: \&quot;KOTA\&quot;,\r\n            \&quot;nama\&quot;: \&quot;MARVIN SUTANTO\&quot;,\r\n            \&quot;tlp\&quot;: \&quot;085885574111\&quot;,\r\n            \&quot;tglLahir\&quot;: \&quot;1991-05-05\&quot;,\r\n            \&quot;provinsi\&quot;: \&quot;\&quot;,\r\n            \&quot;idKtp\&quot;: \&quot;135245306789733321\&quot;,\r\n            \&quot;tmpLahir\&quot;: \&quot;JAKARTA\&quot;,\r\n            \&quot;email\&quot;: \&quot;MARVIN.SUTANTO.TEST@DOCSOL.ID\&quot;,\r\n            \&quot;idPhoto\&quot;: \&quot;\&quot;\r\n        }\r\n    ],\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;confins\&quot;\r\n    }\r\n}&quot;,
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
      <webElementGuid>cf55e8a7-1066-4c74-9a14-f2fd38329fdf</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@WOMF</value>
      <webElementGuid>be30efe5-ceb0-4247-a833-dfcb8405b1f1</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${cloud_url}/services/user/generateInvitationLink</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.cloud_url</defaultValue>
      <description></description>
      <id>b165e6c4-53d4-4170-a3d5-9abe53e0ab7e</id>
      <masked>false</masked>
      <name>cloud_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
