# [ ISIMJacked ] - SIM Hijacking (Part 2)

Ciao!

Here I am, this time, to tell you that my SIM Card is making noises. :3

<figure>
<img align="center" width="1080" height="793" alt="simimage" src="https://github.com/user-attachments/assets/c666a31d-236f-46a2-bfde-95c4bdf67f50" /><br />
<figcaption>Figure 1 - Spaghetti and SIM Cards, UK 2021.</figcaption></figure>
<br />
<br />

This post presents my ongoing research into SIM Cards, building on my previous investigation into [SIM Hijacking](https://sensepost.com/blog/2022/sim-hijacking/). For those unfamiliar with the topic, I recommend starting with the aforementioned blog post, as it provides useful background and context to help better understand the following. However, consider that ...

<p align="center"><b>
... Engaging in unsolicited phone system network testing is illegal and prohibited. Unauthorised testing without a valid license encompasses activities such as manipulating radio frequency channels, intercepting phone system data, and other unauthorised actions.
Before undertaking any form of testing, it is highly advised to possess a comprehensive understanding of the procedures and potential consequences, as well as explicit permission to carry out the testing. It is important to note that this post does not endorse or support any illegal testing practices.</b>
</p>

The following sections explore proactive SIM Card behaviour and introduce an SIM Application Toolkit (STK) applet I developed a while ago for [sysmoISIM-SJA2](https://sysmocom.de/products/sim/sysmousim/index.html) SIM Cards.

## Introduction
The increasing number of SIM Cards tested since the start of this journey has facilitated a lot the research into this matter. Some SIM Cards were obtained during my travels here and there or simply borrowed from friends and colleagues. Additional SIM Cards were instead provided by those who came across my blog post about [SIM Hijacking](https://sensepost.com/blog/2022/sim-hijacking/), and wished to provide support in this research. **Thank you very Manuel!** :)

Testing these SIM Cards revealed that some were vulnerable to SIM hijacking. The tests were conducted using the [SIMTester](https://github.com/srlabs/SIMTester) tool and a PC/SC-compatible card reader.

SIMTester scans and enumerates Toolkit Application References (TARs),  which uniquely identify services and/or applications stored on the SIM Card. It then check the entire TAR range, from `000000` to `FFFFFF`, to identify associated applications that may lack adequate access protection. More details about the TARs values and their associated category may be found in the below table:

| TAR | Category |
| ------------- | ------------- |
| 000000 | Card Manager |
| 000001 to AFFFFF | Allocated by the 1st level application issuer |
| B00000 to B0FFFF | Remote File Management (RFM) |
| B10000 to B1FFFF | Payment application |
| B20000 to BFFEFF | RFU |
| BFFF00 to BFFFFF | Proprietary Toolkit Application |
| C00000 to FFFFFF | Allocated by the 1st level application issuer |

The following figure, instead, shows how the SIMTester output looks like when dealing with a SIM Card vulnerable to Hijacking.

<figure>
<img align="center" width="1080" height="624" alt="2" src="https://github.com/user-attachments/assets/31eff425-f717-4660-8222-c247a84343f1" /><br />
<figcaption>Figure 2 - How the SIMTester report looked when dealing with a SIM card vulnerable to Hijacking.</figcaption></figure>
<br />
<br />
Of course, particular attention was dedicated to SIM Cards that could establish a connection with the surrounding cell towers, by using the Mobile Equipment (ME) in my possession, and that I could easily top up.

## The Behaviour of a Proactive SIM Card
Observing the behavior of a proactive SIM Card provided valuable insights into the communication between the ME, the SUT, and the mobile networks. This approach was way better than sending payloads here and there in the hopes of triggering specific actions.
### First Scenario
As an attempt to intercept some mobile data **purely for learning purposes**, the SUT was hosted by a [Nokia 3330](http://nds2.webapps.microsoft.com/files/support/apac/phones/guides/Nokia_3330_APAC_UG_en.pdf) with the [Net Monitor](https://en.wikipedia.org/wiki/Field_test_mode#Nokia_network_monitor) secret menu enabled. Refer to [SIM Hijacking](https://sensepost.com/blog/2022/sim-hijacking/) to know how to enable this secret menu.

Once again, this task involved the usage of a [F-BUS data cable (Model DAU-05B)](https://www.amazon.co.uk/Hama-Data-Link-Cable-Nokia/dp/B00006J5K0), a [USB to RS232 converter](https://www.ebay.com/itm/332168982978?hash=item4d56d071c2:g:aToAAOSwWxNY1iVy), and the [dct3-gsmtap](https://github.com/osmocom/dct3-gsmtap) utility.
The Net Monitor menu is available on various Nokia phones and consists of a set of displays, where a large amount of information about the SIM Card, the ME, and the network can be accessed. Refer to the [Nokia NetMonitor](http://www.nobbi.com/download/nmmanual.pdf) manual for more information.

<figure>
<img align="center" width="1080" height="1117" alt="3" src="https://github.com/user-attachments/assets/3b6de4ae-a7b9-403f-9935-0b297814e957" /><br />
<figcaption>Figure 3 - The setup.</figcaption></figure>
<br />
<br />

After three minutes of inactivity, a message appeared on the display: `Enviando SMS` (Sending SMS).

<figure><img align="center" width="1080" height="1036" alt="4" src="https://github.com/user-attachments/assets/a9ee057a-202a-41f2-9510-64754a09a5ca" /><br /><figcaption>Figure 4 - The ME displayed "Sending SMS" on its screen after three minutes of inactivity.</figcaption></figure>
<br />
<br />

The reason why this message appeared on the display was because this device has the capability to request notifications when the SIM Card initiates actions, or when it sends out information to the mobile network, with or without being prompted by the network or the device itself. 
This feature was enabled on the device, and it is called `Confirm SIM service actions`. It could be accessed from the settings menu of the Nokia 3330 and/or 3310 by pressing `6`-`2`-`6`. Refer to the following links for more details about the Nokia 3310/3330 and on how to access their menu functions by shortcut: 

* https://www.hmd.com/en_int/support/api/pdf/nokia-3310-user-guide
* https://nokia-3310.helpdoc.net/en/4-using-the-menus/accessing-a-menu-function/by-a-shortcut/

<figure>
<img align="center" width="1080" height="659" alt="5" src="https://github.com/user-attachments/assets/067b665a-7259-414a-b87a-a5635c254ef6" /><br />
<figcaption>Figure 5 - The Nokia 3330 was set to request notifications to confirm service actions.</figcaption></figure>
<br />
<br />

Despite encountering an error while attempting to respond to the SIM service notification due to a lack of signal reception, and being unaware of the actual intention behind this action, the investigation persevered by inspecting the collected data in [Wireshark](https://www.wireshark.org/).

<figure>
<img align="center" width="1080" height="724" alt="6" src="https://github.com/user-attachments/assets/c6195e5d-9a7c-429b-8242-be53c2f9ad8a" /><br />
<figcaption>Figure 6 - According to the display 03 of the Net Monitor menu, no data was shared between the surrounding cell towers and the ME.</figcaption></figure>
<br />
<br />

Unfortunately, to conclude the final episode of this saga of bad luck, many captured packets were malformed. This scenario occurred when the ME issued some APDU commands (APDU-Cs), mostly: `SELECT FILE`, `READ RECORD`, and `PROVIDE LOCAL INFORMATION`.

APDU is a command-response protocol used for invoking functions on smart cards or similar devices. Below is a list of some of the APDU-C instructions (`INS`) available:

| INS  | Description |
| ------------- | ------------- |
| 0E | Erase binary |
| 20 | Verify |
| 70 | Manage channel |
| 82 | External authentication |
| 84 | Get challenge |
| 88 | Internal authentication |
| A4 | Select file |
| B0 | Read binary |
| B2 | Read record(s) |
| C0 | Get response |
| C2 | Envelope |
| CA | Get data |
| D0 | Write binary |
| D2 | Write record |
| D6 | Update binary |
| DA | Put data |
| DC | Update record |
| E2 | Append record |

<figure>
<img align="center" width="1080" height="414" alt="7" src="https://github.com/user-attachments/assets/a95a880f-f7fb-42fd-81a2-ae9f0d25ba5b" /><br />
<figcaption>Figure 7 - The data captured contained multiple malformed packets.</figcaption></figure>
<br />
<br />

<figure>
<img align="center" width="1080" height="542" alt="8" src="https://github.com/user-attachments/assets/0b194d28-932b-48ac-a564-00bd9a453b03" /><br />
<figcaption>Figure 8 - The packets also contained some information related to the menu of the STK application stored on the SUT.</figcaption></figure>
<br />
<br />

The presence of numerous malformed packets suggested potential issues with the equipment used; therefore, no further tests were conducted using the Nokia 3330, the dct3-gsmtap utility, and all the associated hardware components.
### Second Scenario
The 2G connection with the surrounding cell towers could be established without issues when using a more recent phone model. 

Throughout the testing process, a great number of SMS advertisement messages, regarding offers and other related content for the target subscriber were dispatched to the Mobile Station Integrated Services Digital Network (MSISDN) (phone number) associated to the SUT. These SMS messages were possibly originating from either the carrier provider, or potential attackers seeking to execute [Smishing](https://en.wikipedia.org/wiki/Phishing#SMS_phishing) attacks on random phone numbers (MSISDNs).

<figure>
<img align="center" width="1080" height="653" alt="9" src="https://github.com/user-attachments/assets/0757e9e7-10d8-46b4-90d1-619a3078f2f3" /><br />
<figcaption>Figure 9 - An example of a SMS-Point-to-Point (SMS–PP DATA DOWNLOAD) received when hosting the SUT in a more recent phone.</figcaption></figure>
<br />
<br />

<figure>
<img align="center" width="1080" height="624" alt="10" src="https://github.com/user-attachments/assets/3337eec8-15c8-4cdf-b344-95cec2b09774" /><br />
<figcaption>Figure 10 - An example of a Flash SMS message received when hosting the SUT in a more recent phone saying "Is your internet connection dead? Don't miss out! Top up and get the most out of your plan! Visit meuplano.tim.com.br and top up!".</figcaption></figure>
<br />
<br />
  
Since the F-BUS data cable can only be used with a Nokia 3310/3330, this time, a device named [Simtrace2](https://osmocom.org/projects/simtrace2/wiki) was used as a sniffer, and a [GT6000GW](https://www.thevirtualmall.co.uk/products/alibaba/goodcom-gt6000gw-portable-receipt-printer-wifi#capacity=3g) device was instead used as the SIM Card host. The reason of choosing the GT6000GW device for this process was due to its capability to automatically print all incoming SMSs. Any other ME provided by a SIM Card reader would work too but they won't print out incoming SMSs. :P

The SUT was placed in the SIM Card push-pull holder of the Simtrace2 device, which was then connected to the GT6000GW device via a flexible PCB SIM adaptor designed for mini-SIM Cards, also known as `2FF` (Second Form Factor).

<figure>
<img align="center" width="1080" height="570" alt="11" src="https://github.com/user-attachments/assets/2b441d40-429b-4abd-9d89-38e30b53765c" /><br />
<figcaption>Figure 11 - The setup.</figcaption></figure>
<br />
<br />

<figure>
<img align="center" width="1080" height="1255" alt="12" src="https://github.com/user-attachments/assets/e349d451-fed5-4035-a8d6-2bfbc4da3c58" /><br />
<figcaption>Figure 12 - The setup.</figcaption></figure>
<br />
<br />

The usage of the Simtrace2 device, along with Osmocom's [simtrace2-sniff](https://github.com/osmocom/simtrace2) tool, had a positive outcome by enabling the analysis of captured mobile data without any malformed packets.

<figure>
<img align="center" width="1080" height="556" alt="13" src="https://github.com/user-attachments/assets/6a80b9e6-4444-476b-bb1a-e28c19e00646" /><br />
<figcaption>Figure 13 - The data captured contained no malformed packets.</figcaption></figure>
<br />
<br />

<figure>
<img align="center" width="1080" height="379" alt="14" src="https://github.com/user-attachments/assets/30782a2a-8436-4ac7-81c8-4c3c48cfdf92" /><br />
<figcaption>Figure 14 - Using the Osmocom's simtrace2-sniff tool to capture the data generated from the communication established between the SIM Card and the GT6000GW device.</figcaption></figure>
<br />
<br />

Moreover, since all incoming SMSs were printed rather than displayed or shown elsewhere, the likelihood of being deceived by interacting with malicious ones was reduced.

<figure>
<img align="center" width="1080" height="1390" alt="15" src="https://github.com/user-attachments/assets/74f22f9c-5c5f-4be3-ac78-d05a1a1f20fa" /><br />
<figcaption>Figure 15 - The GT600GW device printing incoming SMSs.</figcaption></figure>
<br />
<br />

The `Secure Messaging Indication` error `No SM used between terminal and card` attached to the packets, means that C-APDU's data was transmitted in plaintext.

SM in SIM Cards refers to a security mechanism defined by GlobalPlatform and 3GPP/ETSI standards that ensures the confidentiality, integrity, and confidentiality authenticity of C-APDU and responses exchanged between the SIM card and an external entity such as the mobile network.

<figure>
<img align="center" width="1080" height="283" alt="16" src="https://github.com/user-attachments/assets/b6fb988b-4c22-469b-b644-c83bea8f469e" /><br />
<figcaption>Figure 16 - The Secure Messaging Indication error.</figcaption></figure>
<br />
<br />

The reason of that indication error was potentially due to the equipment used, or the SUT settings. As a matter of fact, the value of the CLA channel encryption bit (or `b6` as it is the sixth bit counting from the right), that indicates the secure messaging feature status and it is used for extended logic channels, was equal to `0`: not enforced.

<figure>
<img align="center" width="1080" height="264" alt="17" src="https://github.com/user-attachments/assets/6cf09581-5006-4f73-bd3d-8c0998eb2974" /><br />
<figcaption>Figure 17 - More details from the European Telecommunications Standards Institute (ETSI). Refer to https://www.etsi.org/deliver/etsi_ts/102200_102299/102221/16.04.00_60/ts_102221v160400p.pdf.</figcaption></figure>
<br />
<br />

Extended logic channels were implemented in newer smart card specifications and standards, such as the [GlobalPlatform Card Specification](https://globalplatform.org/specs-library/). The reason for this upgrade was to enable parallel processing and independent communication with different applications stored on the card, amongst other additional functionalities beyond the basic capabilities provided by standard logic channels (also known as basic logic channels), such as a wider signal range cover, Multimedia Messaging Service (MMS), video calls and more. Refer to the [ETSI](https://www.etsi.org/deliver/etsi_ts/102200_102299/102221/16.04.00_60/ts_102221v160400p.pdf) documentation for more information.

After powering on the devices, the ME (GT600GW) requested the `Short Messages Services Parameters` (SMSP) by selecting the Elementary File (`EF`) named `SMSP`. This file is present on the SIM Card only if `Service No. 12` is active and, in this case, it contained the Short Message Service Centre (SMSC), which was: `+551181138200`.

<figure>
<img align="center" width="1080" height="426" alt="18" src="https://github.com/user-attachments/assets/e7b87317-c30e-4cd9-81c7-6dde823f2ef1" /><br />
<figcaption>Figure 18 - The SMSC MSISDN was successfully returned by the SUT.</figcaption></figure>
<br />
<br />

<figure>
<img align="center" width="1080" height="230" alt="19" src="https://github.com/user-attachments/assets/031acc62-cd68-44b3-9fb7-6a92ecafc92c" /><br />
<figcaption>Figure 19 - Using the  SIMspy II tool to check the status of the SUT services. Refer to http://www.nobbi.com/simspy/simspy.html.</figcaption></figure>
<br />
<br />

Afterwards, the ME initiated the authentication and cipher key generation procedure with the command `RUN GSM ALGORITHM`. The function is used to authenticate the SIM to the mobile network, and it returned the `Signed Response` (SRES) and the `Cryptographic key` (Kc). These values were then stored in the `EF` named `Keyes` with the `UPDATE BINARY` command, always issued by the ME.

The mobile network sends a `Random Number` (RAND) to the MS. Then the ME replies by sending back the `SRES` for comparison and it handles the `Kc` for all subsequent encrypted communications with the mobile network, until the next activation of this mechanism.

<figure>
  <img align="center" width="1080" height="250" alt="20" src="https://github.com/user-attachments/assets/b00cf609-a7ad-4ad9-969f-617a77c96b69" /><br />
<figcaption>Figure 20 - The SRES and Kc returned by the RUN GSM ALGORITHM command.</figcaption></figure>
<br />
<br />

After performing a series of other operations, the SUT executed the proactive command `PROVIDE LOCAL INFORMATION` multiple times to retrieve information about the location from the mobile network where it was registered to and more, such as the `Mobile Country Code` (MCC), the `Mobile Network Code` (MNC), the `Location Area Code` (LAC), the `Tracking Area Code` (TAC), the `Cell Identity`, the `Extended Cell Identity`, and the `IMEI number` associated to the ME.

<figure>
  <img align="center" width="1024" height="392" alt="21" src="https://github.com/user-attachments/assets/bb253ec2-2dff-4e83-a8c6-a094609eb2f8" /><br />
<figcaption>Figure 21 - The response returned by the proactive command PROVIDE LOCAL INFORMATION used to retrieve the MCC, the MNC, the LAC, the TAC, the Cell Identity, and the Extended Cell Identity.</figcaption></figure>
<br />
<br />

<figure>
  <img align="center" width="1200" height="494" alt="22" src="https://github.com/user-attachments/assets/3eb501f9-7867-4d89-99ea-9486a2b7a460" /><br />
<figcaption>Figure 22 - The response returned by the proactive command PROVIDE LOCAL INFORMATION to retrieve the IMEI associated to the ME in use.</figcaption></figure>
<br />
<br />

The proactive command `PROVIDE LOCAL INFORMATION` could be used for multiple purposes depending on the `Command Qualifier` set within it:

<figure>
<img align="center" width="607" height="621" alt="23" src="https://github.com/user-attachments/assets/2b2b465c-218d-4474-8d7e-803734e52b7e" /><br />
<br /><figcaption>Figure 23 - The Command Qualifier 01 was used to retrieve the IMEI number associated to the ME.</figcaption></figure>
<br />
<br />

The below table shows a list of the available `Command Qualifier` values according to ETSI TS 102 223:
| Command Qualifier | Description |
| ------------- | ------------- |
| 00 | Location Information according to current Network Access Application (NAA) |
| 01 | IMEI of the terminal |
| 02 | Network Measurement results according to current NAA |
| 03 | Date, time and time zone |
| 04 | Language setting |
| 05 | Reserved for GSM |
| 06 | Access Technology (single access technology) |
| 07 | ESN of the terminal |
| 08 | MEISV of the terminal |
| 09 | Search Mode |
| 0A | Charge State of the Battery (if class "g" is supported) |
| 0B | MEID of the terminal |
| 0C | Reserved for 3GPP (current WSID) |
| 0D | Broadcast Network information according to current Broadcast Network Technology used |
| 0E | Multiple Access Technologies |
| 0F | Location Information for multiple access technologies |
| 10 | Network Measurement results for multiple access technologies |

At this stage, the SUT finally established a connection to the mobile network. Although the appearance of the SIM service action notification "Enviando SMS" (Sending SMS) was no longer displayed after three minutes of inactivity, this string could still be identified within the captured data from which it emerged, that the information retrieved with the aforementioned steps was sent to the MSISDN associated with the SUT carrier provider with the proactive command `SEND SHORT MESSAGE`.

<figure>
<img align="center" width="1200" height="768" alt="24" src="https://github.com/user-attachments/assets/a7b051b3-fa43-4a48-b3b7-d799ac5556f3" /><br />
<figcaption>Figure 24 - As an example, in this case the SMS with the subscriber related-data was sent through the SMSC stored on the SUT.</figcaption></figure>
<br />
<br />

<figure>
<img align="center" width="1001" height="858" alt="25" src="https://github.com/user-attachments/assets/81b65310-572c-4132-b994-397ec14b64ac" /><br />
<figcaption>Figure 25 - As an example, in this case the SMS with the subscriber related-data was sent to 629.</figcaption></figure>
<br />
<br />

# Analysis of a SIM Application Toolkit
The analysis of the captured data continued for an extended period. This permitted to observe a wider range of events. Some of those events were triggered by the carrier provider, as shown in the below figure, where the SUT attempted to establish a connection with the mobile network using the proactive command named `OPEN CHANNEL`.

<figure>
<img align="center" width="1186" height="806" alt="26" src="https://github.com/user-attachments/assets/45afb493-e8dc-4e09-8e48-f5174fe79a0a" /><br />
<figcaption>Figure 26 - This SUT issued the request to open a channel.</figcaption></figure>
<br />
<br />

The below, instead, is an example of an SMS–PP DATA DOWNLOAD message received. The SMS was sent from
`+551181136710`, a SMSC of the carrier provider. When interacting with what was displayed, a subsequent SMS was sent to MSISDN `8583`.

<figure>
<img align="center" width="1064" height="860" alt="27" src="https://github.com/user-attachments/assets/71704da9-baca-436e-bccc-a2da7aa3b5f7" /><br />
<figcaption>Figure 27 - What was displayed when the SMS– PP data download message arrived said: "Get a TIM Special Credit and stay connected. You'll have enough credit to renew your benefits and only pay for your next recharge. Call *144#".</figcaption></figure>
<br />
<br />

<figure>
<img align="center" width="1075" height="790" alt="29" src="https://github.com/user-attachments/assets/14e7ae73-fee8-4119-b82e-b9c50c8200d9" /><br />
<figcaption>Figure 28 - The sender of the SMS–PP message received was +551181136710.</figcaption></figure>
<br />
<br />

<figure>
<img align="center" width="1200" height="784" alt="28" src="https://github.com/user-attachments/assets/30a0ad75-3a92-4b18-a27c-c9ae3a524acf" /><br />
<figcaption>Figure 29 - The suspicious MSISDN seemed to be a carrier-provider's.</figcaption></figure>
<br />
<br />

A few other interesting events were triggered by interacting with the STK stored on the SUT, which presented itself as shown in the below figure.

<figure>
<img align="center" width="1814" height="3037" alt="31" src="https://github.com/user-attachments/assets/b66de7e6-3a09-4c26-9618-40cc3a4538eb" /><br />
<figcaption>Figure 30 - The STK application menu.</figcaption></figure>
<br />
<br />

First, when opening the STK application stored on the SUT using a modern phone, the command `TERMINAL PROFILE` was triggered and provided information about the capabilities and STK settings of the ME.

<figure>
<img width="1200" height="448" alt="32" src="https://github.com/user-attachments/assets/b1bbe35c-f4da-4a40-83a5-2ca50eaa9f44" /><br />
<figcaption>Figure 31 - The ME capabilities and STK settings.</figcaption></figure>
<br />
<br />

Then, by interacting with the STK application menu items, it was possible to observe some proactive commands that interacted with the famous [S@T Browser](https://trustedconnectivityalliance.org/wp-content/uploads/2020/01/S@T-01.50-v4.0.0-Release-2009.pdf) (SIMalliance Toolbox Browser) application.

<figure>
<img width="1066" height="673" alt="33" src="https://github.com/user-attachments/assets/2b48fce8-57d6-4f2f-bdc9-cba408f9050f" /><br />
<figcaption>Figure 32 - A proactive command that interacted with the S@T Browser application was issued by the SUT when interacting with the STK stored on it.</figcaption></figure>
<br />
<br />

These events worked as a starting point and guideline for attempting to build a custom SIM Card applet that can trigger the aforementioned proactive commands.

# ISIMJacked: A Custom SIM Application Toolkit (STK)
For anyone interested in developing an applet for a SIM Card, this section shows the full process of developing one. Step by step, it highlights the key considerations, tools, and techniques needed to bring a custom SIM Card application to life.

This applet is based on a [Osmocom](https://osmocom.org/)'s [STK applet](https://gitea.osmocom.org/sim-card/hello-stk/) example, and other information that was found on the Internet. It was called ISIMJacked because it shows the behaviour of a few proactive commands that can be leveraged in SIM Hijacking attacks due to inadequate authentication mechanisms, including `DISPLAY TEXT`, `SETUP CALL`, `PLAY TONE`, and others.

It was build with [Ant](https://github.com/martinpaljak/ant-javacard), loaded, and installed on a sysmoISIM-SJA2 SIM Card with the [Shadytel SIM Tools' shadysim_isim_py3.py](https://github.com/hkscy/sim-tools/blob/4bedab1ae5c7a350af66c1827f93f4b621121297/shadysim/shadysim_isim_py3.py) tool.

The sysmoISIM-SJA2 SIM Cards are perfect to learn about applets and how to remotely manage them. The ADM keys, which are required for programming/reading these SIM Cards, were provided by the vendor:

|  |  |  |  |
| ------------- | ------------- | ------------- | ------------- |
| IMSI | ICCID | ACC | PIN1 |
| PUK1 | PIN2 | PUK2 | Ki |
| OPC | ADM1 | KIC1 | KID1 |
| KIK1 | KIC2 | KID2 | KIK2 |
| KIC3 | KID3 |  |  |

<figure>
<img width="1022" height="638" alt="34" src="https://github.com/user-attachments/assets/5598ec20-1b3a-4236-99d6-11ac20c39165" /><br />
<figcaption>Figure 33 - Some sysmoISIM-SJA2 SIM Cards.</figcaption></figure>
<br />
<br />

The below command was used to build an applet with the Ant tool:

`ant`

<figure>
<img width="1200" height="418" alt="3333" src="https://github.com/user-attachments/assets/04b62f93-df31-40ec-86bd-2b8c52afa2fe" /><br />
<figcaption>Figure 34 - Building the ISIMJacked JavaCard applet with the Ant tool.</figcaption></figure>
<br />
<br />

The below command was instead used to load and install ISIMJacked on the Sysmocom SIM Card. During this operation, the Sysmocom SIM Card was placed onto a [Gemalto PC Twin Reader](https://www.amazon.co.uk/IDBridge-CT30-Twin-smart-reader/dp/B00HEMJPYY/ref=sr_1_2?crid=2YC86CE2EQQIH&dib=eyJ2IjoiMSJ9.yzjd4g5Im8z6oo3OAf6TOVKdb5DizvT6r8Yxfwj7DdvhMnsG7xqKu7QAY5Tnltw7.eZmAkRRXuS9kTeqq68NEroHXXKRcnb2rAJBo2qYMRro&dib_tag=se&keywords=Gemalto-IDBridge-CT30&qid=1793438042&s=electronics&sprefix=gemalto-idbridge-ct30%2Celectronics%2C99&sr=1-2) (PC/SC) and this was connected to a host computer.

`python3 simtools/shadysim/shadysim_isim_py3.py --pcsc -l bin/ISIMJacked.cap -i bin/ISIMJacked.cap -kic {KIC1} --kid {KID1} --instance-aid A0000000000102030801 --module-aid A000000000010203080100 --nonvolatile-memoryrequired 0fff --volatile-memory-for-install 0fff --access-domain 00000010 --enable-uicctoolkit --enable-uicc-file-access --max-menuentry-text 20 --max-menu-entries 06`

<figure>
<img width="1200" height="288" alt="444" src="https://github.com/user-attachments/assets/749fe5f7-8774-42ee-85a3-1ca8c8824159" /><br />
  <figcaption>Figure 35 - Loading and installing the ISIMJacked applet on the Sysmocom SIM Card by using shadysim_isim_py3.py.</figcaption></figure>
<br />
<br />

It is important to delete the applet on the SUT prior to repeating this process, otherwise the SIM Card may be bricked. This could be done by using the Shadytel SIM Tools' shadysim_isim_py3.py or with GlobalPlatformPro. In this case the command would be as the following:

`java -jar gp.jar --key-enc KIC1 --key-mac KID1 --key-dek KIK1 –delete A000000000010203080000`

The following figure/video shows how the ISIMJacked applet presented itself once loaded on the sysmoISIM-SJA2 SIM Card.

<figure>
<img width="1814" height="3037" alt="666" src="https://github.com/user-attachments/assets/b30bf58e-4d8d-4ff3-b87f-86ec8f5fb7c3" /><br />
<figcaption>Figure 36 - ISIMJacked.</figcaption></figure>
<br />
<br />

<video src="https://github.com/user-attachments/assets/a1a8d3ab-ddb3-4718-bd96-41123fafe978" controls>
</video>
<figcaption>Video 1 - ISIMJacked.</figcaption></figure>

<br />
<br />

The figure below shows how the Simtrace2 device, combined with Osmocom's simtrace2-sniff tool and Wireshark, was used to identify the above-mentioned proactive commands, which were issued by the ISIMJacked applet when its menu items were invoked.

<figure>
<img width="1022" height="638" alt="34" src="https://github.com/user-attachments/assets/5598ec20-1b3a-4236-99d6-11ac20c39165" /><br />
<figcaption>Figure 37 - ISIMJacked triggered some proactive commands when interacting with it.</figcaption></figure>
<br />
<br />

Very annoying is the fact that repeatedly removing the SIM Card from both the phone and the PC/SC smart card reader during the above-mentioned procedure could potentially cause physical damage to the SIM Card. Until manufacturers introduce a more advanced solution, such as the implementation of landing contacts, to prevent card scratches resulting from long-term friction force, it is recommended to use a smartcard converter to [Nano/Micro/ Standard (2FF/3FF/4FF) SIM/USIM card adaptor](https://www.aliexpress.com/item/32769577127.html) (or [SEEK for Android](https://seek-for-android.github.io/)) and a normal PC/SC smart card reader.

<figure>
<img width="1801" height="3037" alt="5555" src="https://github.com/user-attachments/assets/d1e1b25c-b542-4cf2-8b60-9c878eac1bb6" /><br />
<figcaption>Figure 38 - A suffering SIM Card chip with evident signs of excessive usage caused by the hosting SIM-Card-connector-header of the ME.</figcaption></figure>
<br />
<br />

# Conclusion
Theere is still a lot of content to be written concerning this matter, but I am going to stop here. For now.
In this blog post, we looked at:

* The behavior of a proactive SIM Card
* What a SIM Card applet looks like and what it does
* How to create a custom SIM Card applet

Thanks for reading.

<p align="center">
Proudly made by one of those lazy Italians.<br />
HAPPY HACKING! <1337</p>
