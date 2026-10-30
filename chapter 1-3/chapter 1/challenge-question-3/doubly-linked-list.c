// Do the same thing for C. To get some practice with pointers,
// define a doubly-linked list of heap-allocated strings.
// Write functions to insert, find, and delete items from it. Test them.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct Node {
    char *data;
    struct Node *next;
    struct Node *prev;
} Node;

// Insert a string at the front
void insert(Node **head, const char *text) {
    Node *newNode = malloc(sizeof(Node));
    if (newNode == NULL) exit(1);

    newNode->data = malloc(strlen(text) + 1);
    if (newNode->data == NULL) { free(newNode); exit(1); }
    strcpy(newNode->data, text);

    newNode->prev = NULL;
    newNode->next = *head;

    if (*head != NULL) {
        (*head)->prev = newNode;
    }

    *head = newNode;
}

// Find a string
Node *find(Node *head, const char *text) {
    Node *current = head;

    while (current != NULL) {
        if (strcmp(current->data, text) == 0) {
            return current;
        }

        current = current->next;
    }

    return NULL;
}

// Delete a node containing the string
void delete(Node **head, const char *text) {
    Node *node = find(*head, text);

    if (node == NULL) {
        return;
    }

    if (node->prev != NULL) {
        node->prev->next = node->next;
    } else {
        *head = node->next;
    }

    if (node->next != NULL) {
        node->next->prev = node->prev;
    }

    free(node->data);
    free(node);
}

// Print the list
void printList(Node *head) {
    Node *current = head;

    while (current != NULL) {
        printf("%s\n", current->data);
        current = current->next;
    }
}

int main() {
    printf("Hello, World!\n");

    Node *head = NULL;

    insert(&head, "apple");
    insert(&head, "banana");
    insert(&head, "orange");

    printf("List:\n");
    printList(head);

    Node *result = find(head, "banana");

    if (result != NULL) {
        printf("\nFound: %s\n", result->data);
    }

    delete(&head, "banana");

    printf("\nAfter deleting banana:\n");
    printList(head);

    while (head != NULL) delete(&head, head->data);
    return 0;
}
