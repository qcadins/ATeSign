<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Update Data User</name>
   <tag></tag>
   <elementGuidId>85b0af03-828a-4fa0-a536-83a16628f46e</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>false</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n    \&quot;audit\&quot;: {\n        \&quot;callerId\&quot;: \&quot;${callerId}\&quot;\n    },\n    \&quot;idMsuser\&quot;  : ${idMsUser},\n    \&quot;email\&quot;     : \&quot;${email}\&quot;,\n    \&quot;provinsi\&quot;  : \&quot;${provinsi}\&quot;,\n    \&quot;kota\&quot;      : \&quot;${kota}\&quot;,\n    \&quot;kecamatan\&quot; : \&quot;${kecamatan}\&quot;,\n    \&quot;gender\&quot;    : \&quot;${gender}\&quot;,\n    \&quot;kelurahan\&quot; : \&quot;${kelurahan}\&quot;,\n    \&quot;zipCode\&quot;   : \&quot;${zipCode}\&quot;,\n    \&quot;dob\&quot;       : \&quot;${dob}\&quot;,\n    \&quot;pob\&quot;       : \&quot;${pob}\&quot;,\n    \&quot;idNo\&quot;      : \&quot;${idNo}\&quot;,\n    \&quot;address\&quot;   : \&quot;${address}\&quot;,\n    \&quot;name\&quot;      : \&quot;${name}\&quot;,\n    \&quot;phone\&quot;     : \&quot;${phone}\&quot;\n}\n&quot;,
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
      <webElementGuid>41526b17-f619-474f-80fd-9108c560ccf4</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>4e7cca04-6c80-44f3-bec3-08e71a4931ee</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer ${token}</value>
      <webElementGuid>420b592f-1a9b-4398-8363-637a3d8ac8c2</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/user/s/updateDataUser</restUrl>
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
      <id>73578dec-c1e6-4075-9ddc-6a6a6718e481</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.token</defaultValue>
      <description></description>
      <id>c2bb1d65-4fc2-4326-9622-0a3670ec095d</id>
      <masked>false</masked>
      <name>token</name>
   </variables>
   <verificationScript>import static org.assertj.core.api.Assertions.*

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.verification.WSResponseManager

import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable

RequestObject request = WSResponseManager.getInstance().getCurrentRequest()

ResponseObject response = WSResponseManager.getInstance().getCurrentResponse()</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
