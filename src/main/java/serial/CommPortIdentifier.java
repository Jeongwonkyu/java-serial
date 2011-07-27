// SMSLib for Java v3
// A Java API library for sending and receiving SMS via a GSM modem
// or other supported gateways.
// Web Site: http://www.smslib.org
//
// Copyright (C) 2002-2009, Thanasis Delenikas, Athens/GREECE.
// SMSLib is distributed under the terms of the Apache License version 2.0
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package serial;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Communications port management.
 * <p>
 * <b>Please note: </b>This is a wrapper around
 * <code>javax.comm.CommPortIdentifier</code> (and so
 * <code>gnu.io.CommPortIdentifier</code>). The API definition is taken from
 * Sun. So honor them!
 * </p>
 * <code>CommPortIdentifier</code> is the central class for controlling access
 * to communications ports. It includes methods for:
 * </p>
 * <ul>
 * <li> Determining the communications ports made available by the driver. </li>
 * <li> Opening communications ports for I/O operations. </li>
 * <li> Determining port ownership. </li>
 * <li> Resolving port ownership contention. </li>
 * <li> Managing events that indicate changes in port ownership status. </li>
 * </ul>
 * <p>
 * An application first uses methods in <code>CommPortIdentifier</code> to
 * negotiate with the driver to discover which communication ports are available
 * and then select a port for opening. It then uses methods in other classes
 * like <code>CommPort</code>, <code>ParallelPort</code> and
 * <code>SerialPort</code> to communicate through the port.
 * </p> *
 * 
 * @author gwellisch
 */
public class CommPortIdentifier extends SerialClassWrapper
{
	/**
	 * @see gnu.io.CommPortIdentifier#PORT_SERIAL
	 * @see javax.comm.CommPortIdentifier#PORT_SERIAL
	 */
	public static final int PORT_SERIAL;
	static
	{
		Class<?> classCommPortIdentifier = SerialClassFactory.getInstance().forName(CommPortIdentifier.class);
		try
		{
			PORT_SERIAL = ReflectionHelper.getStaticInt(classCommPortIdentifier, "PORT_SERIAL");
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

//> CONSTRUCTORS
	/** @see SerialClassWrapper#SerialClassWrapper(Object) */
	private CommPortIdentifier(Object realObject) {
		super(realObject);
	}
	
//> ACCESSORS
	/**
	 * Returns the port type.
	 * 
	 * @return portType - PORT_SERIAL or PORT_PARALLEL
	 * @see gnu.io.CommPortIdentifier#getPortType()
	 * @see javax.comm.CommPortIdentifier#getPortType()
	 */
	public int getPortType() {
		return super.invokeWithoutInvocationException(int.class, "getPortType");
	}

	/**
	 * Returns the name of the port.
	 * 
	 * @return the name of the port
	 * @see gnu.io.CommPortIdentifier#getName()
	 * @see javax.comm.CommPortIdentifier#getName()
	 */
	public String getName() {
		return super.invokeWithoutInvocationException(String.class, "getName");
	}

	/**
	 * @return <code>true</code> if this port is currently owned; <code>false</code> otherwise.
	 * @see gnu.io.CommPortIdentifier#isCurrentlyOwned()
	 * @see javax.comm.CommPortIdentifier#isCurrentlyOwned()
	 */
	public boolean isCurrentlyOwned() {
		return super.invokeWithoutInvocationException(boolean.class, "isCurrentlyOwned");
	}

	/**
	 * @return A string identifying the current owner of this port.
	 * @see gnu.io.CommPortIdentifier#getCurrentOwner()
	 * @see javax.comm.CommPortIdentifier#getCurrentOwner()
	 */
	public String getCurrentOwner() {
		return super.invokeWithoutInvocationException(String.class, "getCurrentOwner");
	}

	/**
	 * @param appname
	 * @param timeout
	 * @return The {@link SerialPort} we have opened
	 * @throws PortInUseException If the desired port is already opened by someone else
	 * @see gnu.io.CommPortIdentifier#open(String, int)
	 * @see javax.comm.CommPortIdentifier#open(String, int)
	 */
	public SerialPort open(String appname, int timeout) throws PortInUseException
	{
		Class<?>[] paramTypes = new Class<?>[] { String.class, int.class };
		Method method = ReflectionHelper.getMethod(SerialClassFactory.getInstance().forName(CommPortIdentifier.class), "open", paramTypes);
		try {
			return new SerialPort(ReflectionHelper.invoke(Object.class, method, this.getRealObject(), appname, timeout));
		} catch (InvocationTargetException ex) {
			SerialException.throwIfMatches(PortInUseException.class, ex);
			throw new IllegalStateException(ex);
		}
	}

//> PUBLIC STATIC METHODS
	/**
	 * Obtains an enumeration object that contains a
	 * <code>CommPortIdentifier</code> object for each port in the system.
	 *
	 * This method is synchronized to prevent strange things happening when reloading the config via {@link #refreshListIfRequired()}.
	 * 
	 * @return <code>Enumeration</code> that can be used to enumerate all the
	 *         ports known to the system
	 */
	@SuppressWarnings("unchecked")
	public static synchronized Enumeration<CommPortIdentifier> getPortIdentifiers()
	{		
		// get the enumeration of real objects
		Class<?> commPortIdentifierClass = SerialClassFactory.getInstance().forName(CommPortIdentifier.class);
		refreshListIfRequired(commPortIdentifierClass);
		Method method = ReflectionHelper.getMethod(commPortIdentifierClass, "getPortIdentifiers");
		Enumeration list = ReflectionHelper.invokeWithoutInvocationException(Enumeration.class, method, null);
		
		// wrap the real objects
		Vector<CommPortIdentifier> vec = new Vector<CommPortIdentifier>();
		while (list.hasMoreElements())
			vec.add(new CommPortIdentifier(list.nextElement()));
		return vec.elements();
	}
	
	/**
	 * Refresh list of available COM ports.  This method works around a bug with Java COM API
	 * that leads to ports being cached.  For more info on the bug, you could see
	 * http://forum.java.sun.com/thread.jspa?threadID=575580&messageID=2986928 but Oracle have
	 * killed the link :(
	 * 
	 * This method should only be called from {@link #getPortIdentifiers()}
	 */
	private static void refreshListIfRequired(Class<?> commPortIdentifierClass) {
		if(SerialClassFactory.PACKAGE_JAVAXCOMM.equals(SerialClassFactory.getInstance().getSerialPackageName())) {
			try {
				Field masterIdList = commPortIdentifierClass.getDeclaredField("masterIdList");
				masterIdList.setAccessible(true);
				masterIdList.set(null, null);
	
				Method loadDriver = commPortIdentifierClass.getDeclaredMethod("loadDriver", String.class);
				loadDriver.setAccessible(true);
				loadDriver.invoke(null, SerialClassFactory.javaxCommPropertiesPath);
			} catch(Exception ex) {
				System.err.println("There was an error trying to reset javax.comm ports cache: " + ex + " : " + ex.getMessage()); // TODO consider logging this properly?
			}
		}
	}

	/**
	 * Obtains a CommPortIdentifier object by using a port name. The port name
	 * may have been stored in persistent storage by the application.
	 * 
	 * @param portName
	 *            name of the port to open
	 * @return <code>CommPortIdentifier</code> object
	 * @throws NoSuchPortException 
	 * @throws RuntimeException
	 *             (wrapping a NoSuchPortException) if the port does not exist
	 */
	public static CommPortIdentifier getPortIdentifier(String portName) throws NoSuchPortException
	{
		//get the string of real objects
		Method method = ReflectionHelper.getMethod(SerialClassFactory.getInstance().forName(CommPortIdentifier.class), "getPortIdentifier", String.class);
		CommPortIdentifier port;
		try {
			port = new CommPortIdentifier(ReflectionHelper.invoke(Object.class, method, null, portName));
		} catch (InvocationTargetException invocationException) {
			SerialException.throwIfMatches(NoSuchPortException.class, invocationException);
			throw new IllegalStateException(invocationException);
		}
		
		return port;
	}
}
