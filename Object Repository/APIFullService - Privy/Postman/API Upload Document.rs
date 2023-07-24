<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>API Upload Document</name>
   <tag></tag>
   <elementGuidId>ecf0f038-d553-4a3f-a630-c62024f06152</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;parameters&quot;: [
    {
      &quot;name&quot;: &quot;documentTitle&quot;,
      &quot;value&quot;: &quot;ANDYTEST004&quot;,
      &quot;type&quot;: &quot;text&quot;,
      &quot;contentType&quot;: &quot;&quot;
    },
    {
      &quot;name&quot;: &quot;docType&quot;,
      &quot;value&quot;: &quot;Parallel&quot;,
      &quot;type&quot;: &quot;text&quot;,
      &quot;contentType&quot;: &quot;&quot;
    },
    {
      &quot;name&quot;: &quot;owner&quot;,
      &quot;value&quot;: &quot;{\&quot;privyId\&quot;:\&quot;ADD2843\&quot;, \&quot;enterpriseToken\&quot;: \&quot;{{enterprise_token}}\&quot; }&quot;,
      &quot;type&quot;: &quot;text&quot;,
      &quot;contentType&quot;: &quot;&quot;
    },
    {
      &quot;name&quot;: &quot;recipients&quot;,
      &quot;value&quot;: &quot;[ { \&quot;privyId\&quot;: \&quot;MNI5025\&quot;, \&quot;type\&quot;: \&quot;Signer\&quot;, \&quot;enterpriseToken\&quot;: \&quot;\&quot;}, { \&quot;privyId\&quot;: \&quot;ADD2843\&quot;, \&quot;type\&quot;: \&quot;Signer\&quot;, \&quot;enterpriseToken\&quot;: \&quot;\&quot;,\&quot;draggable\&quot;: \&quot;false\&quot; } ]&quot;,
      &quot;type&quot;: &quot;text&quot;,
      &quot;contentType&quot;: &quot;&quot;
    },
    {
      &quot;name&quot;: &quot;document&quot;,
      &quot;type&quot;: &quot;file&quot;,
      &quot;contentType&quot;: &quot;&quot;
    }
  ]
}</httpBodyContent>
   <httpBodyType>form-data</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Basic e3t1c2VybmFtZX19Ont7cGFzc3dvcmR9fQ==</value>
      <webElementGuid>a4726a36-0c8e-48be-a46d-2e3fb9126e21</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>merchant-key</name>
      <type>Main</type>
      <value>${merchant-key}</value>
      <webElementGuid>d69988e3-ca95-4c4d-a7f2-0787e24951a0</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.5.5</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>https://stg-core.privy.id/v3/merchant/document/upload</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.merchant-key</defaultValue>
      <description></description>
      <id>9a3fec14-bc93-47b1-a200-480d6a6554b2</id>
      <masked>false</masked>
      <name>merchant-key</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
