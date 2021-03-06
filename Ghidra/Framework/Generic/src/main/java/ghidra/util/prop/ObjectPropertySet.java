/* ###
 * IP: GHIDRA
 * REVIEWED: YES
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.util.prop;

import java.io.*;

/**
 * Handles general storage and retrieval of object values indexed by long keys.
 *
 */
public class ObjectPropertySet extends PropertySet {
    private final static long serialVersionUID = 1;
	/**
	 * Constructor for ObjectPropertySet.
	 * @param name the name associated with this property set.
	 */
	public ObjectPropertySet(String name) {
		super(name, null);
	}

	/**
	 * @see PropertySet#getDataSize()
	 */
	@Override
    public int getDataSize() {
		return 20;
	}

	/**
	 * Stores an object at the given index.  Any object currently at that index
	 * will be replaced by the new object.
	 * @param index the index at which to store the object.
	 * @param value the object to store.
	 */
	public void putObject(long index, Object value) {
		PropertyPage page = getOrCreatePage(getPageID(index));
		int n = page.getSize();
		page.addObject(getPageOffset(index), value);
		numProperties += page.getSize() - n;
	}

	/**
	 * Retrieves the object stored at the given index.
	 * @param index the index at which to retrieve the object.
	 * @return the object stored at the given index or null if no object is
	 * stored at the index.
	 */
	public Object getObject(long index) {
		PropertyPage page = getPage(getPageID(index));
		if (page != null) {
			return page.getObject(getPageOffset(index));
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see ghidra.util.prop.PropertySet#moveIndex(long, long)
	 */
	@Override
    protected void moveIndex(long from, long to) {
		Object value = getObject(from);
		remove(from);
		putObject(to, value);
	}
	/**
	 * saves the property at the given index to the given output stream.
	 */
	@Override
    protected void saveProperty(ObjectOutputStream oos, long index) throws IOException {
		oos.writeObject(getObject(index));
	}
	/**
	 * restores the property from the input stream to the given index.
	 */
	@Override
    protected void restoreProperty(ObjectInputStream ois, long index)
	 	throws IOException, ClassNotFoundException {

		putObject(index, ois.readObject());
	}

	/**
	 * 
	 * @see ghidra.util.prop.PropertySet#applyValue(PropertyVisitor, long)
	 */
	@Override
    public void applyValue(PropertyVisitor visitor, long addr) {
		Object obj = getObject(addr);
		if (obj != null) {
			visitor.visit(obj);
		}
	}

}
