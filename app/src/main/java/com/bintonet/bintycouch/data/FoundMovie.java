package com.bintonet.bintycouch.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Darren on 19/08/2014.
 */
public class FoundMovie implements List<FoundMovie> {

//    public String poster;
    public String title;
    public String imdb;

    public String getFoundMovieTitle() {
        return this.title;
    }
    public void setFoundMovieTitle(String foundMovieTitle) {
        this.title = title;
    }

    public String getFoundMovieIMDB() {
        return imdb;
    }
    public void setFoundMovieIMDB(String imdb) {
        this.imdb = imdb;
    }

    @Override
    public void add(int location, FoundMovie object) {

    }

    @Override
    public boolean add(FoundMovie object) {
        return false;
    }

    @Override
    public boolean addAll(int location, Collection<? extends FoundMovie> collection) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends FoundMovie> collection) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean contains(Object object) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public FoundMovie get(int location) {
        return null;
    }

    @Override
    public int indexOf(Object object) {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Iterator<FoundMovie> iterator() {
        return null;
    }

    @Override
    public int lastIndexOf(Object object) {
        return 0;
    }

    @Override
    public ListIterator<FoundMovie> listIterator() {
        return null;
    }

    @Override
    public ListIterator<FoundMovie> listIterator(int location) {
        return null;
    }

    @Override
    public FoundMovie remove(int location) {
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public FoundMovie set(int location, FoundMovie object) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public List<FoundMovie> subList(int start, int end) {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return null;
    }
}
