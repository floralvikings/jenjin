package com.jenjinstudios.world.collections;

import com.jenjinstudios.world.object.WorldObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Caleb Brinkman
 */
public class WorldObjectList implements List<WorldObject>
{
	private final Map<Integer, WorldObject> objects = new ConcurrentHashMap<>();
	private final Map<Integer, WorldObject> toAdd = new ConcurrentHashMap<>();
	private final Map<Integer, WorldObject> toRemove = new ConcurrentHashMap<>();

	public void refresh() {
		toRemove.keySet().forEach(objects::remove);
		toRemove.clear();
		toAdd.entrySet().stream().
			  filter(entry -> !objects.containsKey(entry.getKey())).
			  forEach(entry -> {
				  objects.put(entry.getKey(), entry.getValue());
			  });
		toAdd.clear();
	}

	@Override
	public void forEach(Consumer<? super WorldObject> action) {
		synchronized (objects) { objects.values().forEach(action); }
	}

	private int getUniqueId() {
		int i = -1;
		boolean unique = false;
		while (!unique)
		{
			i++;
			unique = !objects.containsKey(i) && !toAdd.containsKey(i);
		}
		return i;
	}

	/**
	 * Return the number of WorldObjects in the main list.
	 * @return the number of elements in this list
	 */
	@Override
	public int size() { return objects.size(); }

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 * @return <tt>true</tt> if this list contains no elements
	 */
	@Override
	public boolean isEmpty() { return objects.isEmpty(); }

	/**
	 * Returns <tt>true</tt> if this list contains the specified element. More formally, returns <tt>true</tt> if and
	 * only if this list contains at least one element <tt>e</tt> such that <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;
	 * :&nbsp;o.equals(e))</tt>.
	 * @param o element whose presence in this list is to be tested
	 * @return <tt>true</tt> if this list contains the specified element
	 * @throws ClassCastException if the type of the specified element is incompatible with this list (<a
	 * href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements (<a
	 * href="Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public boolean contains(Object o) {
		boolean contains = false;
		if (o instanceof WorldObject)
		{
			int id = ((WorldObject) o).getId();
			contains = contains(id);
		}
		return contains;
	}

	public boolean contains(int id) { return objects.containsKey(id); }

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 * @return an iterator over the elements in this list in proper sequence
	 */
	@Override
	public Iterator<WorldObject> iterator() { return objects.values().iterator(); }

	/**
	 * Returns an array containing all of the elements in this list in proper sequence (from first to last element).
	 * <p>
	 * <p>The returned array will be "safe" in that no references to it are maintained by this list.  (In other words,
	 * this method must allocate a new array even if this list is backed by an array). The caller is thus free to
	 * modify
	 * the returned array.
	 * <p>
	 * <p>This method acts as bridge between array-based and collection-based APIs.
	 * @return an array containing all of the elements in this list in proper sequence
	 * @see Arrays#asList(Object[])
	 */
	@Override
	public Object[] toArray() { return objects.values().toArray(); }

	/**
	 * Returns an array containing all of the elements in this list in proper sequence (from first to last element);
	 * the
	 * runtime type of the returned array is that of the specified array.  If the list fits in the specified array, it
	 * is returned therein.  Otherwise, a new array is allocated with the runtime type of the specified array and the
	 * size of this list.
	 * <p>
	 * <p>If the list fits in the specified array with room to spare (i.e., the array has more elements than the list),
	 * the element in the array immediately following the end of the list is set to <tt>null</tt>. (This is useful in
	 * determining the length of the list <i>only</i> if the caller knows that the list does not contain any null
	 * elements.)
	 * <p>
	 * <p>Like the {@link #toArray()} method, this method acts as bridge between array-based and collection-based APIs.
	 * Further, this method allows precise control over the runtime type of the output array, and may, under certain
	 * circumstances, be used to save allocation costs.
	 * <p>
	 * <p>Suppose <tt>x</tt> is a list known to contain only strings. The following code can be used to dump the list
	 * into a newly allocated array of <tt>String</tt>:
	 * <p>
	 * <pre>{@code
	 *     String[] y = x.toArray(new String[0]);
	 * }</pre>
	 * <p>
	 * Note that <tt>toArray(new Object[0])</tt> is identical in function to <tt>toArray()</tt>.
	 * @param a the array into which the elements of this list are to be stored, if it is big enough; otherwise, a new
	 * array of the same runtime type is allocated for this purpose.
	 * @return an array containing the elements of this list
	 * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime type of
	 * every element in this list
	 * @throws NullPointerException if the specified array is null
	 */
	@Override
	public <T> T[] toArray(T[] a) { return objects.values().toArray(a); }

	/**
	 * Appends the specified element to the end of this list (optional operation).
	 * <p>
	 * <p>Lists that support this operation may place limitations on what elements may be added to this list.  In
	 * particular, some lists will refuse to add null elements, and others will impose restrictions on the type of
	 * elements that may be added.  List classes should clearly specify in their documentation any restrictions on what
	 * elements may be added.
	 * @param worldObject element to be appended to this list
	 * @return <tt>true</tt> (as specified by {@link Collection#add})
	 * @throws UnsupportedOperationException if the <tt>add</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of the specified element prevents it from being added to this list
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements
	 * @throws IllegalArgumentException if some property of this element prevents it from being added to this list
	 */
	@Override
	public boolean add(WorldObject worldObject) {
		if (worldObject == null) throw new NullPointerException("Cannot add null WorldObject");
		int uniqueId = getUniqueId();
		worldObject.setId(uniqueId);
		synchronized (toAdd)
		{
			toAdd.put(uniqueId, worldObject);
		}
		return true;
	}

	/**
	 * Removes the first occurrence of the specified element from this list, if it is present (optional operation).  If
	 * this list does not contain the element, it is unchanged.  More formally, removes the element with the lowest
	 * index <tt>i</tt> such that <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt> (if such an
	 * element exists).  Returns <tt>true</tt> if this list contained the specified element (or equivalently, if this
	 * list changed as a result of the call).
	 * @param o element to be removed from this list, if present
	 * @return <tt>true</tt> if this list contained the specified element
	 * @throws ClassCastException if the type of the specified element is incompatible with this list (<a
	 * href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements (<a
	 * href="Collection.html#optional-restrictions">optional</a>)
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this list
	 */
	@Override
	public boolean remove(Object o) {
		if (o == null) throw new NullPointerException("Cannot remove null WorldObject");
		if (!(o instanceof WorldObject)) throw new IllegalArgumentException("o must be WorldObject");
		WorldObject worldObject = (WorldObject) o;
		int id = worldObject.getId();
		boolean changed = objects.containsKey(id);
		if (changed)
		{
			toRemove.put(id, worldObject);
		}
		return changed;
	}

	/**
	 * Returns <tt>true</tt> if this list contains all of the elements of the specified collection.
	 * @param c collection to be checked for containment in this list
	 * @return <tt>true</tt> if this list contains all of the elements of the specified collection
	 * @throws ClassCastException if the types of one or more elements in the specified collection are incompatible
	 * with
	 * this list (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified collection contains one or more null elements and this list does
	 * not permit null elements (<a href="Collection.html#optional-restrictions">optional</a>), or if the specified
	 * collection is null
	 * @see #contains(Object)
	 */
	@Override
	public boolean containsAll(Collection<?> c) { return objects.values().containsAll(c); }

	/**
	 * Appends all of the elements in the specified collection to the end of this list, in the order that they are
	 * returned by the specified collection's iterator (optional operation).  The behavior of this operation is
	 * undefined if the specified collection is modified while the operation is in progress.  (Note that this will
	 * occur
	 * if the specified collection is this list, and it's nonempty.)
	 * @param c collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>addAll</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of an element of the specified collection prevents it from being
	 * added to
	 * this list
	 * @throws NullPointerException if the specified collection contains one or more null elements and this list does
	 * not permit null elements, or if the specified collection is null
	 * @throws IllegalArgumentException if some property of an element of the specified collection prevents it from
	 * being added to this list
	 * @see #add(Object)
	 */
	@Override
	public boolean addAll(Collection<? extends WorldObject> c) {
		c.forEach(this::add);
		return true;
	}

	/**
	 * Inserts all of the elements in the specified collection into this list at the specified position (optional
	 * operation).  Shifts the element currently at that position (if any) and any subsequent elements to the right
	 * (increases their indices).  The new elements will appear in this list in the order that they are returned by the
	 * specified collection's iterator.  The behavior of this operation is undefined if the specified collection is
	 * modified while the operation is in progress.  (Note that this will occur if the specified collection is this
	 * list, and it's nonempty.)
	 * @param index index at which to insert the first element from the specified collection
	 * @param c collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>addAll</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of an element of the specified collection prevents it from being
	 * added to
	 * this list
	 * @throws NullPointerException if the specified collection contains one or more null elements and this list does
	 * not permit null elements, or if the specified collection is null
	 * @throws IllegalArgumentException if some property of an element of the specified collection prevents it from
	 * being added to this list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends WorldObject> c) {
		throw new UnsupportedOperationException("WorldObjectList does not support adding collections at indices.");
	}

	/**
	 * Removes from this list all of its elements that are contained in the specified collection (optional operation).
	 * @param c collection containing elements to be removed from this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of an element of this list is incompatible with the specified collection
	 * (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if this list contains a null element and the specified collection does not permit
	 * null elements (<a href="Collection.html#optional-restrictions">optional</a>), or if the specified collection is
	 * null
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		c.forEach(this::remove);
		return true;
	}

	/**
	 * Removes from this list all of its elements that are contained in the specified collection (optional operation).
	 * @param c collection containing elements to be removed from this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of an element of this list is incompatible with the specified collection
	 * (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if this list contains a null element and the specified collection does not permit
	 * null elements (<a href="Collection.html#optional-restrictions">optional</a>), or if the specified collection is
	 * null
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("RetainAll is not supported in WorldObjectList");
	}

	/**
	 * Removes all of the elements from this list (optional operation). The list will be empty after this call returns.
	 * @throws UnsupportedOperationException if the <tt>clear</tt> operation is not supported by this list
	 */
	@Override
	public void clear() {
		objects.clear();
		toAdd.clear();
		toRemove.clear();
	}

	/**
	 * Returns the element at the specified position in this list.
	 * @param index index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	@Override
	public WorldObject get(int index) { return objects.get(index); }

	/**
	 * Replaces the element at the specified position in this list with the specified element (optional operation).
	 * @param index index of the element to replace
	 * @param element element to be stored at the specified position
	 * @return the element previously at the specified position
	 * @throws UnsupportedOperationException if the <tt>set</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of the specified element prevents it from being added to this list
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this
	 * list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	@Override
	public WorldObject set(int index, WorldObject element) {
		if (objects.containsKey(index))
		{
			toRemove.put(index, element);
			toAdd.put(index, element);
		} else
		{
			toAdd.put(index, element);
		}
		return null;
	}

	/**
	 * Inserts the specified element at the specified position in this list (optional operation).  Shifts the element
	 * currently at that position (if any) and any subsequent elements to the right (adds one to their indices).
	 * @param index index at which the specified element is to be inserted
	 * @param element element to be inserted
	 * @throws UnsupportedOperationException if the <tt>add</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of the specified element prevents it from being added to this list
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this
	 * list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
	 */
	@Override
	public void add(int index, WorldObject element) {
		throw new UnsupportedOperationException("This operation requires modifying existing IDs.");
	}

	/**
	 * Removes the element at the specified position in this list (optional operation).  Shifts any subsequent elements
	 * to the left (subtracts one from their indices).  Returns the element that was removed from the list.
	 * @param index the index of the element to be removed
	 * @return the element previously at the specified position
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	@Override
	public WorldObject remove(int index) {
		WorldObject worldObject = objects.get(index);
		if (worldObject != null)
		{
			toRemove.put(index, worldObject);
		}
		return worldObject;
	}

	/**
	 * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the lowest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>, or -1 if there is no such index.
	 * @param o element to search for
	 * @return the index of the first occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element
	 * @throws ClassCastException if the type of the specified element is incompatible with this list (<a
	 * href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements (<a
	 * href="Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public int indexOf(Object o) {
		final int[] index = {-1};
		synchronized (objects)
		{
			objects.keySet().stream().filter(k -> k == o).findFirst().ifPresent(i -> index[0] = i);
		}
		return index[0];
	}

	/**
	 * Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the highest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>, or -1 if there is no such index.
	 * @param o element to search for
	 * @return the index of the last occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element
	 * @throws ClassCastException if the type of the specified element is incompatible with this list (<a
	 * href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements (<a
	 * href="Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public int lastIndexOf(Object o) {
		final int[] index = {-1};
		synchronized (objects)
		{
			objects.keySet().stream().filter(k -> k == o).forEach(i -> index[0] = i);
		}
		return index[0];
	}

	/**
	 * This method is not supported by the WorldObjectList class.
	 * @return This method will never successfully return.
	 */
	@Override
	public ListIterator<WorldObject> listIterator() {
		throw new UnsupportedOperationException("WorldObjectLists currently do not support list iterators.");
	}

	/**
	 * This method is not supported by the WorldObjectList class.
	 * @return This method will never successfully return.
	 */
	@Override
	public ListIterator<WorldObject> listIterator(int index) {
		throw new UnsupportedOperationException("WorldObjectLists currently do not support list iterators.");
	}

	/**
	 * Returns a view of the portion of this list between the specified <tt>fromIndex</tt>, inclusive, and
	 * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex</tt> and <tt>toIndex</tt> are equal,
	 * the returned list is empty.)
	 * The returned list is backed by this list, so non-structural changes in the returned list are reflected in this
	 * list, and vice-versa. The returned list supports all of the optional list operations supported by this list.<p>
	 * <p>
	 * This method eliminates the need for explicit range operations (of the sort that commonly exist for arrays).  Any
	 * operation that expects a list can be used as a range operation by passing a subList view instead of a whole
	 * list.
	 * For example, the following idiom removes a range of elements from a list:
	 * <pre>{@code
	 *      list.subList(from, to).clear();
	 * }</pre>
	 * Similar idioms may be constructed for <tt>indexOf</tt> and <tt>lastIndexOf</tt>,
	 * and all of the algorithms in the
	 * <tt>Collections</tt> class can be applied to a subList.<p>
	 * <p>
	 * The semantics of the list returned by this method become undefined if the backing list (i.e., this list) is
	 * <i>structurally modified</i> in any way other than via the returned list.  (Structural modifications are those
	 * that change the size of this list, or otherwise perturb it in such a fashion that iterations in progress may
	 * yield incorrect results.)
	 * @param fromIndex low endpoint (inclusive) of the subList
	 * @param toIndex high endpoint (exclusive) of the subList
	 * @return a view of the specified range within this list
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value (<tt>fromIndex &lt; 0 || toIndex &gt; size
	 * || fromIndex &gt; toIndex</tt>)
	 */
	@Override
	public List<WorldObject> subList(int fromIndex, int toIndex) {
		List<WorldObject> subList = new LinkedList<>();
		synchronized (objects)
		{
			objects.keySet().stream().
				  filter(k -> k >= fromIndex && k < toIndex).
				  forEach(k -> subList.add(objects.get(k)));
		}
		return subList;
	}
}
