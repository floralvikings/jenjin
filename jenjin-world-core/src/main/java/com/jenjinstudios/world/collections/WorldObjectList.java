package com.jenjinstudios.world.collections;

import com.jenjinstudios.world.WorldObject;

import java.util.*;

/**
 * @author Caleb Brinkman
 */
public class WorldObjectList implements List<WorldObject>
{
	private final Map<Integer, WorldObject> objects = new HashMap<>();
	private final Map<Integer, WorldObject> toAdd = new HashMap<>();
	private final Map<Integer, WorldObject> toRemove = new HashMap<>();

	public void refresh() {
		synchronized (toRemove)
		{
			synchronized (objects)
			{
				toRemove.keySet().forEach(objects::remove);
			}
			toRemove.clear();
		}
		synchronized (toAdd)
		{
			synchronized (objects)
			{
				toAdd.entrySet().stream().
					  filter(entry -> !objects.containsKey(entry.getKey())).
					  forEach(entry -> objects.put(entry.getKey(), entry.getValue()));
			}
			toAdd.clear();
		}
	}

	private int getUniqueId() {
		int i = -1;
		boolean unique = false;
		while (!unique)
		{
			i++;
			synchronized (objects)
			{
				if (!objects.containsKey(i))
				{
					unique = true;
				}
			}
			if (unique)
			{
				synchronized (toAdd)
				{
					if (toAdd.containsKey(i))
					{
						unique = false;
					}
				}
			}
		}
		return i;
	}

	@Override
	public int size() { synchronized (objects) { return objects.size(); } }

	@Override
	public boolean isEmpty() { synchronized (objects) { return objects.isEmpty(); } }

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

	public boolean contains(int id) { synchronized (objects) { return objects.containsKey(id); } }

	@Override
	public Iterator<WorldObject> iterator() { synchronized (objects) { return objects.values().iterator(); } }

	@Override
	public Object[] toArray() { synchronized (objects) { return objects.values().toArray(); } }

	@Override
	public <T> T[] toArray(T[] a) { synchronized (objects) { return objects.values().toArray(a); } }

	@Override
	public boolean add(WorldObject worldObject) {
		if (worldObject == null) throw new NullPointerException("Cannot add null WorldObject");
		int uniqueId = getUniqueId();
		worldObject.setId(uniqueId);
		synchronized (toAdd)
		{
			toAdd.put(uniqueId, worldObject);
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Should schedule for removal
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) { synchronized (objects) { return objects.values().containsAll(c); } }

	@Override
	public boolean addAll(Collection<? extends WorldObject> c) {
		// TODO Should schedule for addition
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends WorldObject> c) {
		// TODO Should schedule for overwrite
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Should schedule for removal
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Should schedule for removal
		return false;
	}

	@Override
	public void clear() {
		// TODO Should schedule for removal
	}

	@Override
	public WorldObject get(int index) { synchronized (objects) { return objects.get(index); } }

	@Override
	public WorldObject set(int index, WorldObject element) {
		// TODO Should schedule for overwrite
		return null;
	}

	@Override
	public void add(int index, WorldObject element) {
		// TODO Should schedule for overwrite
	}

	@Override
	public WorldObject remove(int index) {
		// TODO Should schedule for removal
		return null;
	}

	@Override
	public int indexOf(Object o) {
		final int[] index = {-1};
		synchronized (objects)
		{
			objects.keySet().stream().filter(k -> k == o).findFirst().ifPresent(i -> index[0] = i);
		}
		return index[0];
	}

	@Override
	public int lastIndexOf(Object o) {
		final int[] index = {-1};
		synchronized (objects)
		{
			objects.keySet().stream().filter(k -> k == o).forEach(i -> index[0] = i);
		}
		return index[0];
	}

	@Override
	public ListIterator<WorldObject> listIterator() {
		// TODO Not sure how this works
		throw new UnsupportedOperationException("WorldObjectLists currently do not support list iterators.");
	}

	@Override
	public ListIterator<WorldObject> listIterator(int index) {
		// TODO Not sure how this works
		throw new UnsupportedOperationException("WorldObjectLists currently do not support list iterators.");
	}

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
