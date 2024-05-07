<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Check Document Before Signing</name>
   <tag></tag>
   <elementGuidId>a3318b16-e2c9-4740-9af3-b148a653b6fe</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;msg\&quot;: \&quot;Pvkay+O8L4Wg/1CNGMLhW21BjlToc9IcZxoHcrGf/miEt8B4Hz8toY4TXGChGu/6swyir60BfJ3S+LjNhstnLA\u003d\u003d\&quot;,\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;vendorCode\&quot; : \&quot;VIDA\&quot;,\r\n        \&quot;listDocumentId\&quot;: [\r\n        \&quot;CN48HtkuYOR7kyE4BdAo2LUJZ4bF7uRyOt22M/W2MNOU6b2M1gC+0tUGvbPHHrDb\&quot;,\r\n        \&quot;CZF2V0Dv6Ji+mIvg7hAwXvk1VslVGfdIUHzzX7guI1ECFpTi0eDcUY5EnbvpeYQp\&quot;\r\n    ],\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;a\&quot;\r\n    }\r\n}&quot;,
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
      <webElementGuid>ed9140e8-87e2-4539-ac00-16dbf7a0caf5</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>6938ad6e-1981-45fe-93a9-ec9610a3c887</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/embed/document/checkDocumentBeforeSigningEmbed</restUrl>
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
      <id>e37792a5-aed5-496a-bd51-f7ebfff7a6a3</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
