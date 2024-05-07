<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Save Feedback Copy</name>
   <tag></tag>
   <elementGuidId>01a833f9-7daf-4a07-967b-836491945617</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n  \&quot;audit\&quot;: {\r\n    \&quot;callerId\&quot;: \&quot;andy\&quot;\r\n  },\r\n  \&quot;loginId\&quot;: \&quot;andy@ad-ins.com\&quot;,\r\n  \&quot;feedbackValue\&quot;: 5,\r\n  \&quot;comment\&quot;: \&quot;tes feedback 5\&quot;,\r\n  \&quot;documentId\&quot;: \&quot;HpKnJ/zF3NYTJKqDKmYNrON7X1WLrNgQK6n7670HFvGNzGDboSZR9IR1U05O2A9j\&quot;,\r\n  \&quot;msg\&quot; : \&quot;F5Ms6hRdpCzzhelbgo9HIQLWL0QiGikrezRipyFuKd3e1t4a7v2Iitgk2PzUt2y4kdZ\&quot;\r\n}&quot;,
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
      <webElementGuid>2d2c8aef-0382-441b-9571-195fd1f100f2</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>3278a1ab-502f-4370-8cc5-cd7bef5eb963</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/feedback/saveEmbed</restUrl>
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
      <id>6e0a5236-8b64-408c-8642-8c857bc653c8</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
