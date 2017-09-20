/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TextInput,
  Button
} from 'react-native';
import SidEscDriver from './SidEscDriver';

export default class testPrint extends Component {
  constructor(props) {
    super(props);
    this.state = {ip:'192.168.55.1'};
  }

  test = () =>{
    this.print().then(()=>{
      alert('Success');
    }).catch((e)=>{
      alert('Failed');
      alert(e);
    });
  }
 
  print = async () => {
    SidEscDriver.SetInitial(this.state.ip, 9100, 48);
    SidEscDriver.AddText('Hello World');
    SidEscDriver.AddText('Hello World');
    SidEscDriver.AddFeed('10');
    SidEscDriver.AddCut();
    try{
        let x = await SidEscDriver.Sent();
        return new Promise.resolve();
    }catch(e){
        return new Promise.reject(e);
    }
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
         Sample Print Using Native Java!
        </Text>
        <Text style={styles.instructions}>
          Printer IP Address
        </Text>
        <TextInput
    style={{alignSelf:'stretch'}}
    value={this.state.ip}
    onChangeText = {(val)=> this.setState({ip:val})}
  />
    <Button
    onPress = {() => this.test()}
    title="Test Print"
    color="#841584"
  />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-start',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('testPrint', () => testPrint);
