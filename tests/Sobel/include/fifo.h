/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2012)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
#ifndef FIFO_H
#define FIFO_H

/**
 * Item of the FIFO
 */
typedef struct fifo_item{
	void* item;
	struct fifo_item *next;
} fifo_item;

/**
 * FIFO structure
 */
typedef struct fifo{
	fifo_item* head;
	int count;
} fifo;

/**
 * Initialize multiples fifo structures.
 * @param fifo Array of structures to initialize
 * @param size Count of structures to initialize
 */
void fifos_init(fifo *fifo, int size);

/**
 * Push a data into the FIFO
 * @param fifo FIFO pointer
 * @param item Pointer of the item to store.
 * @param size Size of the item.
 */
void push(fifo *fifo, void* item, int size);

/**
 * Pull a data into the FIFO
 * @param fifo FIFO pointer
 * @param item Pointer of the destination of the data to retrieve.
 * @param size Size of the item.
 */
void pull(fifo *fifo, void* item, int size);

#endif//FIFO_H