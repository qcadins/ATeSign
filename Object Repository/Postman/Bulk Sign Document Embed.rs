<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Bulk Sign Document Embed</name>
   <tag></tag>
   <elementGuidId>b55dde0a-2a34-458c-8327-f26534a6967c</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n  \&quot;audit\&quot;: {\r\n    \&quot;callerId\&quot;: \&quot;string\&quot;\r\n  },\r\n    \&quot;documentIds\&quot;: [\r\n    \&quot;G0J3O26tTZ2+LLM6en1VvZNDPpEZMSecouosYZ3k2IJXf8+xJ3saTPfHKv4wF+nr\&quot;,\r\n    \&quot;G0J3O26tTZ2+LLM6en1VvdCfAaanAmSo/q8poQcayKVPwV6TDgcjm78wggFWcYuw\&quot;\r\n  ]\r\n    ,\&quot;msg\&quot; : \&quot;uCJVQ/PggDtKKd+eo20mBAiTYhlhURGxbHk3uDX9mYbe1t4a7v2Iitgk2PzUt2y4PSuNVLz49cRe9WiUWw/6jN5ou5rmzcZuXccKfE2mksw\u003d\&quot;\r\n}&quot;,
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
      <webElementGuid>0f2703eb-c2d5-400b-ab63-bc163b424600</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>eb128f75-5eb5-45ef-8603-1e7d901e5174</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer o+mBHDs4I5Nn8sxLVAzbyvXsU98=</value>
      <webElementGuid>1fad874d-3dc5-44ae-bd47-ec6a5f1e675b</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/document/bulkSignDocumentEmbed</restUrl>
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
      <id>0bf89687-7e7b-46c3-82ee-0dfa4af482e0</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
