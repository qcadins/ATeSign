<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Request Link Malvin</name>
   <tag></tag>
   <elementGuidId>a70cbe0d-a47b-459f-8516-a16dae94f131</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;mobile\&quot;\r\n    },\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;users\&quot;: [\r\n        {\r\n            \&quot;email\&quot;: \&quot;MALVIN@ANDYRESEARCH.MY.ID\&quot;,\r\n            \&quot;nama\&quot;: \&quot;MALVIN\&quot;,\r\n            \&quot;tlp\&quot;: \&quot;089693475025\&quot;,\r\n            \&quot;jenisKelamin\&quot;: \&quot;M\&quot;,\r\n            \&quot;tmpLahir\&quot;: \&quot;PONTIANAK\&quot;,\r\n            \&quot;tglLahir\&quot;: \&quot;\&quot;,\r\n            \&quot;idKtp\&quot;: \&quot;6112011506950017\&quot;,\r\n            \&quot;provinsi\&quot;: \&quot;KALIMANTAN BARAT\&quot;,\r\n            \&quot;kota\&quot;: \&quot;PONTIANAK\&quot;,\r\n            \&quot;kecamatan\&quot;: \&quot;Sungai Raya\&quot;,\r\n            \&quot;kelurahan\&quot;: \&quot;Parti Baru\&quot;,\r\n            \&quot;kodePos\&quot;: \&quot;78391\&quot;,\r\n            \&quot;alamat\&quot;: \&quot;Komp. PIL 19 No.34\&quot;\r\n        }\r\n    ]\r\n}&quot;,
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
      <webElementGuid>4104acf3-4392-4186-9b91-46a3c1ff58de</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@WOMF</value>
      <webElementGuid>4466e7ba-b31f-43d0-aef6-71f8615840ca</webElementGuid>
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
      <id>59a36bc6-1549-449b-bf72-dee7b8b5a0aa</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
