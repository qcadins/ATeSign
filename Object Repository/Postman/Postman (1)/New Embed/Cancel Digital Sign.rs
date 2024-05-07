<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Cancel Digital Sign</name>
   <tag></tag>
   <elementGuidId>77bc1063-c668-4568-afd9-7afbe2362024</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;msg\&quot;: \&quot;jJwIXY4SY5WmgDDCFMaeezsXZMLFVqawcPY2Iu6jc4mcHEDuFFKwdecZNFtGoINgvC2D9nupk+43dvRJMNAK7g\u003d\u003d\&quot;,\r\n    \&quot;tenantCode\&quot;: \&quot;TAFS\&quot;,\r\n    \&quot;documentId\&quot; : \&quot;ys1ldkLUVepHH/OWClPbeMp9y6LjvED2ByJ7rSe9gKbqSErNPe2p/JB54VV6zUSq\&quot;,\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;ANDY-CHECK\&quot;\r\n    }\r\n}&quot;,
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
      <webElementGuid>52db49e8-45fa-49d2-b7b0-0634f4d280aa</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>b6d4dac7-752c-4ae8-b2a3-f42153f59a26</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/embed/document/cancelDigitalSign</restUrl>
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
      <id>00c52a05-dda0-4799-860f-da8f63de28eb</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
