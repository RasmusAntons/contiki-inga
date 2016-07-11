<?xml version="1.0" encoding="UTF-8"?>
<simconf>
  <project EXPORT="discard">[APPS_DIR]/mrm</project>
  <project EXPORT="discard">[APPS_DIR]/mspsim</project>
  <project EXPORT="discard">[APPS_DIR]/avrora</project>
  <project EXPORT="discard">[APPS_DIR]/serial_socket</project>
  <project EXPORT="discard">[APPS_DIR]/collect-view</project>
  <project EXPORT="discard">[APPS_DIR]/powertracker</project>
  <simulation>
    <title>runicast</title>
    <randomseed>generated</randomseed>
    <motedelay_us>1000000</motedelay_us>
    <radiomedium>
      org.contikios.cooja.radiomediums.UDGM
      <transmitting_range>50.0</transmitting_range>
      <interference_range>100.0</interference_range>
      <success_ratio_tx>1.0</success_ratio_tx>
      <success_ratio_rx>1.0</success_ratio_rx>
    </radiomedium>
    <events>
      <logoutput>40000</logoutput>
    </events>
    <motetype>
      org.contikios.cooja.avrmote.IngaMoteType
      <identifier>Inga1</identifier>
      <description>motorrelay</description>
      <source EXPORT="discard">[CONTIKI_DIR]/workspace/broadcast/motorrelay/motorrelay.c</source>
      <commands EXPORT="discard">make motorrelay.inga TARGET=inga</commands>
      <firmware EXPORT="copy">[CONTIKI_DIR]/workspace/broadcast/motorrelay/motorrelay.inga</firmware>
      <moteinterface>org.contikios.cooja.interfaces.Position</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraMoteID</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraLED</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AT86RF23xRadio</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraClock</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraUsart0</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvrDebugger</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraADC</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.Mote2MoteRelations</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.MoteAttributes</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.RimeAddress</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.IPAddress</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.SensorInterface</moteinterface>
    </motetype>
    <motetype>
      org.contikios.cooja.avrmote.IngaMoteType
      <identifier>Inga2</identifier>
      <description>motorcontrol</description>
      <source EXPORT="discard">[CONTIKI_DIR]/workspace/broadcast/motorcontrol/motorcontrol.c</source>
      <commands EXPORT="discard">make motorcontrol.inga TARGET=inga</commands>
      <firmware EXPORT="copy">[CONTIKI_DIR]/workspace/broadcast/motorcontrol/motorcontrol.inga</firmware>
      <moteinterface>org.contikios.cooja.interfaces.Position</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraMoteID</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraLED</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AT86RF23xRadio</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraClock</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraUsart0</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvrDebugger</moteinterface>
      <moteinterface>org.contikios.cooja.avrmote.interfaces.AvroraADC</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.Mote2MoteRelations</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.MoteAttributes</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.RimeAddress</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.IPAddress</moteinterface>
      <moteinterface>org.contikios.cooja.interfaces.SensorInterface</moteinterface>
    </motetype>
    <mote>
      <breakpoints />
      <interface_config>
        org.contikios.cooja.interfaces.Position
        <x>6.651788984875381</x>
        <y>34.67096513321721</y>
        <z>0.0</z>
      </interface_config>
      <interface_config>
        org.contikios.cooja.avrmote.interfaces.AvroraMoteID
        <id>1</id>
      </interface_config>
      <interface_config>
        org.contikios.cooja.avrmote.interfaces.AvroraUsart0
        <history>l1~;</history>
      </interface_config>
      <interface_config>
        org.contikios.cooja.interfaces.SensorInterface
        <sensor>ADXL345</sensor>
        <sensor>Button</sensor>
        <sensor>L3G4200D</sensor>
        <sensor>BMP085</sensor>
      </interface_config>
      <motetype_identifier>Inga1</motetype_identifier>
    </mote>
    <mote>
      <breakpoints />
      <interface_config>
        org.contikios.cooja.interfaces.Position
        <x>50.49114301570735</x>
        <y>34.82600203312679</y>
        <z>0.0</z>
      </interface_config>
      <interface_config>
        org.contikios.cooja.avrmote.interfaces.AvroraMoteID
        <id>2</id>
      </interface_config>
      <interface_config>
        org.contikios.cooja.interfaces.SensorInterface
        <sensor>ADXL345</sensor>
        <sensor>Button</sensor>
        <sensor>L3G4200D</sensor>
        <sensor>BMP085</sensor>
      </interface_config>
      <motetype_identifier>Inga2</motetype_identifier>
    </mote>
    <mote>
      <breakpoints />
      <interface_config>
        org.contikios.cooja.interfaces.Position
        <x>91.20414050367307</x>
        <y>35.57450386365867</y>
        <z>0.0</z>
      </interface_config>
      <interface_config>
        org.contikios.cooja.avrmote.interfaces.AvroraMoteID
        <id>3</id>
      </interface_config>
      <interface_config>
        org.contikios.cooja.interfaces.SensorInterface
        <sensor>ADXL345</sensor>
        <sensor>Button</sensor>
        <sensor>L3G4200D</sensor>
        <sensor>BMP085</sensor>
      </interface_config>
      <motetype_identifier>Inga2</motetype_identifier>
    </mote>
    <mote>
      <breakpoints />
      <interface_config>
        org.contikios.cooja.interfaces.Position
        <x>135.3445028107224</x>
        <y>35.135980865203045</y>
        <z>0.0</z>
      </interface_config>
      <interface_config>
        org.contikios.cooja.avrmote.interfaces.AvroraMoteID
        <id>4</id>
      </interface_config>
      <interface_config>
        org.contikios.cooja.interfaces.SensorInterface
        <sensor>ADXL345</sensor>
        <sensor>Button</sensor>
        <sensor>L3G4200D</sensor>
        <sensor>BMP085</sensor>
      </interface_config>
      <motetype_identifier>Inga2</motetype_identifier>
    </mote>
  </simulation>
  <plugin>
    org.contikios.cooja.plugins.SimControl
    <width>280</width>
    <z>2</z>
    <height>160</height>
    <location_x>400</location_x>
    <location_y>0</location_y>
  </plugin>
  <plugin>
    org.contikios.cooja.plugins.Visualizer
    <plugin_config>
      <moterelations>true</moterelations>
      <skin>org.contikios.cooja.plugins.skins.IDVisualizerSkin</skin>
      <skin>org.contikios.cooja.plugins.skins.GridVisualizerSkin</skin>
      <skin>org.contikios.cooja.plugins.skins.TrafficVisualizerSkin</skin>
      <skin>org.contikios.cooja.plugins.skins.UDGMVisualizerSkin</skin>
      <viewport>1.8319017134712654 0.0 0.0 1.8319017134712654 62.467459638174034 101.7721148361942</viewport>
    </plugin_config>
    <width>400</width>
    <z>1</z>
    <height>400</height>
    <location_x>1</location_x>
    <location_y>1</location_y>
  </plugin>
  <plugin>
    org.contikios.cooja.plugins.LogListener
    <plugin_config>
      <filter />
      <formatted_time />
      <coloring />
    </plugin_config>
    <width>827</width>
    <z>3</z>
    <height>240</height>
    <location_x>400</location_x>
    <location_y>160</location_y>
  </plugin>
  <plugin>
    org.contikios.cooja.plugins.TimeLine
    <plugin_config>
      <mote>0</mote>
      <mote>1</mote>
      <mote>2</mote>
      <mote>3</mote>
      <showRadioRXTX />
      <showRadioHW />
      <showLEDs />
      <zoomfactor>500.0</zoomfactor>
    </plugin_config>
    <width>1227</width>
    <z>9</z>
    <height>166</height>
    <location_x>0</location_x>
    <location_y>732</location_y>
  </plugin>
  <plugin>
    org.contikios.cooja.plugins.Notes
    <plugin_config>
      <notes>Enter notes here</notes>
      <decorations>true</decorations>
    </plugin_config>
    <width>547</width>
    <z>8</z>
    <height>160</height>
    <location_x>680</location_x>
    <location_y>0</location_y>
  </plugin>
  <plugin>
    org.contikios.cooja.plugins.MoteInterfaceViewer
    <mote_arg>0</mote_arg>
    <plugin_config>
      <interface>Log Output</interface>
      <scrollpos>0,0</scrollpos>
    </plugin_config>
    <width>277</width>
    <z>7</z>
    <height>185</height>
    <location_x>4</location_x>
    <location_y>460</location_y>
  </plugin>
  <plugin>
    org.contikios.cooja.plugins.MoteInterfaceViewer
    <mote_arg>1</mote_arg>
    <plugin_config>
      <interface>Log Output</interface>
      <scrollpos>0,0</scrollpos>
    </plugin_config>
    <width>277</width>
    <z>6</z>
    <height>185</height>
    <location_x>308</location_x>
    <location_y>456</location_y>
  </plugin>
  <plugin>
    org.contikios.cooja.plugins.MoteInterfaceViewer
    <mote_arg>2</mote_arg>
    <plugin_config>
      <interface>Log Output</interface>
      <scrollpos>0,0</scrollpos>
    </plugin_config>
    <width>277</width>
    <z>5</z>
    <height>185</height>
    <location_x>617</location_x>
    <location_y>451</location_y>
  </plugin>
  <plugin>
    org.contikios.cooja.plugins.MoteInterfaceViewer
    <mote_arg>3</mote_arg>
    <plugin_config>
      <interface>Log Output</interface>
      <scrollpos>0,0</scrollpos>
    </plugin_config>
    <width>277</width>
    <z>4</z>
    <height>185</height>
    <location_x>919</location_x>
    <location_y>450</location_y>
  </plugin>
  <plugin>
    org.contikios.cooja.plugins.SimControl
    <width>280</width>
    <z>0</z>
    <height>160</height>
    <location_x>400</location_x>
    <location_y>0</location_y>
  </plugin>
</simconf>

